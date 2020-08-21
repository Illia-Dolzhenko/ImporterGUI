package util;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Uploader extends SwingWorker<Boolean, String> {

    private Path imageDirectory;
    private String URL;
    private String login;
    private String password;
    private Printer printer;
    private DoneListener doneListener;

    public Uploader(Printer printer, String imageDirectory, String URL, String login, String password) {
        super();
        this.imageDirectory = Path.of(imageDirectory);
        this.URL = URL;
        this.login = login;
        this.password = password;
        this.printer = printer;
    }

    public void addDoneListener(DoneListener doneListener) {
        this.doneListener = doneListener;
    }

    private List<Path> loadImages() throws IOException {
        try (Stream<Path> stream = Files.walk(imageDirectory, 1)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .collect(Collectors.toList());
        }
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        FTPClient client = new FTPClient();
        client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        client.setControlEncoding("UTF-8");

        client.connect(URL);
        int replyCode = client.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            client.disconnect();
        }

        client.login(login, password);
        client.setFileType(FTP.BINARY_FILE_TYPE);
        client.enterLocalPassiveMode();

        List<String> existingFiles = Stream.of(client.listFiles()).map(FTPFile::getName).collect(Collectors.toList());
        List<Path> localImages = loadImages();
        List<Path> imagesToUpload = new ArrayList<>();

        localImages.forEach(image -> {
            if (!existingFiles.contains(image.getFileName().toString())) {
                imagesToUpload.add(image);
            }
        });

        publish("[INFO] (" + imagesToUpload.size() + ") new images found.");

        for (int i = 0; i < imagesToUpload.size(); i++) {
            if (isCancelled()) {
                disconnect(client);
                break;
            }
            Path image = imagesToUpload.get(i);
            publish("[INFO] Uploading file " + i + " / " + imagesToUpload.size());
            try (InputStream fileStream = new FileInputStream(image.toFile())) {
                client.storeFile(image.getFileName().toString(), fileStream);
            }
        }

        disconnect(client);

        return true;
    }

    @Override
    protected void process(List<String> chunks) {
        String message = chunks.get(chunks.size() - 1);
        printer.print(message);
    }

    @Override
    protected void done() {
        try {
            get();
            printer.print("[INFO] Upload completed");
            doneListener.done();
        } catch (InterruptedException e) {
            printer.print("[ERROR] Uploader thread is interrupted!");
            doneListener.done();
            e.printStackTrace();
        } catch (ExecutionException e) {
            printer.print("[ERROR] Can't upload files");
            printer.print(e.getMessage());
            doneListener.done();
            e.printStackTrace();
        } catch (CancellationException e){
            printer.print("[INFO] Uploader thread is stopped");
        }
    }

    private void disconnect(FTPClient client) throws IOException {
        if (client.isConnected()) {
            client.logout();
            client.disconnect();
        }
    }
}
