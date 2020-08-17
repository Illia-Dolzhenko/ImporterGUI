package util;

import entity.Product;
import exception.AppException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Importer {

    private String urlPrefix;
    private List<Product> products;
    private Path catalogLocation;
    private Printer printer;

    public Importer(Printer printer) {
        this.printer = printer;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public Path getCatalogLocation() {
        return catalogLocation;
    }

    public void setCatalogLocation(Path catalogLocation) {
        this.catalogLocation = catalogLocation;
    }

    public void loadProducts() throws AppException {
        try {
            products = readProducts();
        } catch (IOException e) {
            e.printStackTrace();
            throw new AppException("[ERROR] Can't read products!");
        }
        printer.print("[INFO] Loaded " + products.size() + " products.");
        checkProducts(products);
    }

    public void saveImages() throws AppException {
        products.forEach(product -> {
            try {
                copyImage(product);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        printer.print("[INFO] Images saved to \\images folder");

        try {
            checkImages(products);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AppException("[WARN] Can't check saved images.");
        }
    }

    public void saveProductsToCSV() throws AppException {

        if (urlPrefix == null || urlPrefix.isBlank()) {
            throw new AppException("[ERROR] Url prefix is no set.");
        }

        if (products == null || products.size() < 1) {
            throw new AppException("[ERROR] Products are not loaded.");
        }

        if (catalogLocation == null) {
            throw new AppException("[ERROR] Catalog is not selected.");
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Type, sku, Regular price, Attribute 1 name, Attribute 1 value(s), Categories, Name, Images, Description\n");
        products.forEach(product -> stringBuilder
                .append("virtual")
                .append(",")
                .append(product.getSKU())
                .append(",").append(product.getWeight())
                .append(",").append("Вес,")
                .append(product.getWeight())
                .append(",")
                .append(product.getCategories())
                .append(",")
                .append(product.getName()).append(" (").append(product.getSKU()).append(")")
                .append(",").append(urlPrefix).append(product.getSKU()).append(".jpg")
                .append(",")
                .append(product.getDescription())
                .append("\n"));
        try {
            Files.write(Paths.get(catalogLocation.getParent().toString() + "\\products.csv"), stringBuilder.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
            printer.print("[INFO] Csv file saved: " + catalogLocation.getParent().toString() + "\\products.csv");
        } catch (IOException e) {
            printer.print("[ERROR] Error occurred while saving csv file.");
            e.printStackTrace();
        }

        saveCategories();
    }

    public void showProducts() {
        if (products == null) {
            return;
        }
        products.forEach(product -> printer.print(product.toString()));
        printer.print("[INFO] Number of products: " + products.size());
    }

    public List<Product> getProducts() {
        if (products != null) {
            return new ArrayList<>(products);
        }
        return null;
    }

    private void saveCategories() {

        Set<String> categories = new HashSet<>();

        products.forEach(product -> {
            List<String> productCategories = product.getRawCategories();
            StringBuilder category = new StringBuilder();
            for (int i = productCategories.size() - 1; i >= 0; i--) {
                category.append(productCategories.get(i)).append("$");
                for (int j = i; j < productCategories.size(); j++) {
                    category.append(productCategories.get(j).replace(" ","-").toLowerCase());
                    if (j != productCategories.size() - 1) {
                        category.append("-");
                    }
                }
                if (i != 0) {
                    category.append("/");
                }
            }
            categories.add(category.toString());
        });

        StringBuilder result = new StringBuilder();

        categories.forEach(category -> {
            result.append(category).append(System.lineSeparator());
        });

        try {
            Files.write(Paths.get(catalogLocation.getParent().toString() + "\\categories.txt"), result.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
            printer.print("[INFO] Txt file saved: " + catalogLocation.getParent().toString() + "\\categories.txt");
        } catch (IOException e) {
            printer.print("[ERROR] Error occurred while saving txt file.");
            e.printStackTrace();
        }
    }

    private void checkSKUsUnique(List<Product> products) {
        List<String> uniqueSKUs = new ArrayList<>();
        products.forEach(product -> {
            String sku = product.getSKU();
            if (!uniqueSKUs.contains(sku)) {
                uniqueSKUs.add(sku);
            } else {
                //System.out.println("[WARN] Product has not unique SKU: " + product);
                printer.print("[WARN] Product has not unique SKU: " + product);
            }
        });
    }

    private void checkImages(List<Product> products) throws IOException {
        List<Path> files = listFilesUsingFileWalk(Paths.get(catalogLocation.getParent().toString() + "\\images\\").toString());
        printer.print("[INFO] Number of image files: " + files.size());
        List<String> skus = files.stream().map(file ->
                file.getFileName()
                        .toString()
                        .split("\\.")[0]
                        .toLowerCase(new Locale("ru", "RU")))
                .collect(Collectors.toList());
        List<String> productSkus = products
                .stream()
                .map(product -> product.getSKU().toLowerCase(new Locale("ru", "RU")))
                .collect(Collectors.toList());
        productSkus.forEach(productSku -> {
            if (!skus.contains(productSku)) {
                printer.print("[ERROR] Product doesn't have image file: " + productSku);
            }
        });
    }

    private void checkProducts(List<Product> products) {
        products.forEach(product -> {
            try {
                Float.parseFloat(product.getWeight().trim());
            } catch (NumberFormatException e) {
                //System.out.println("[WARN] Product has wrong weight value: " + product.lessInfo());
                printer.print("[WARN] Product has wrong weight value: " + product.lessInfo());
            }
        });
        checkSKUsUnique(products);
    }

    private void copyImage(Product product) throws IOException {
        Path original = product.getFile();
        Path copied = Paths.get(catalogLocation.getParent().toString() + "\\images\\");
        if (!Files.exists(copied)) {
            try {
                Files.createDirectories(copied);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Files.copy(original, Paths.get(catalogLocation.getParent().toString() + "\\images\\" + product.getSKU() + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
    }

    private List<Product> readProducts() throws IOException {
        List<Path> files = listFilesUsingFileWalk(catalogLocation.toString());
        List<Product> products = new ArrayList<>();
        Map<String, String> names = readNames();

        files.forEach(file -> {
            String fileExtension = file.getFileName().toString().split("\\.")[1];

            if ("jpg".equals(fileExtension.toLowerCase())) {
                Product product = new Product();
                List<String> categories = getAllParents(file, catalogLocation.getFileName().toString()).stream().map(path -> path.getFileName().toString()).collect(Collectors.toList());
                List<String> info = Arrays.asList(file.getFileName().toString().split(" "));

                product.setCategories(categories);
                product.setSKU(info.get(0));
                product.setWeight(info.get(1).replace(",", "."));
                product.setFile(file);

                StringBuilder description = new StringBuilder();
                for (int i = 3; i < info.size(); i++) {
                    description.append(info.get(i)).append(" ");
                }

                product.setDescription(description.toString().split("\\.")[0].trim());

                if (names == null) {
                    product.setName("Unknown");
                } else {
                    names.keySet().forEach(key -> {
                        if (product.getSKU().startsWith(key)) {
                            product.setName(names.get(key));
                        }
                    });
                    if (product.getName() == null) {
                        product.setName("Unknown");
                    }
                }
                products.add(product);
            }
        });
        return products;
    }

    private List<Path> listFilesUsingFileWalk(String dir) throws IOException {
        try (Stream<Path> stream = Files.walk(Paths.get(dir), 10)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .collect(Collectors.toList());
        }
    }

    private List<Path> getAllParents(Path path, String nameOfCatalogFolder) {
        List<Path> result = new ArrayList<>();
        Path parent = path.getParent();
        while (parent != null && !nameOfCatalogFolder.equals(parent.getFileName().toString())) {
            result.add(parent);
            parent = parent.getParent();
        }
        return result;
    }

    private Map<String, String> readNames() {
        Path path = Paths.get(catalogLocation.toString() + "\\names.txt");

        Stream<String> lines;
        try {
            lines = Files.lines(path);
        } catch (IOException e) {
            System.out.println("Can't read file: " + path.toString());
            e.printStackTrace();
            return null;
        }

        Map<String, String> names = new HashMap<>();

        lines.forEach(line -> {
            String[] values = line.split(";");
            names.put(values[0], values[1]);
        });
        lines.close();
        return names;
    }

}
