package entity;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Product {
    private String SKU;
    private String weight;
    private String name;
    private String description;
    private List<String> categories;
    private Path file;
    private List<Path> additionalImages = new ArrayList<>();

    public List<Path> getAdditionalImages() {
        return additionalImages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Path getFile() {
        return file;
    }

    public void setFile(Path file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategories() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = categories.size() - 1; i >= 0; i--) {
            if (i != 0) {
                stringBuilder.append(categories.get(i)).append(" > ");
            } else {
                stringBuilder.append(categories.get(i));
            }
        }
        return stringBuilder.toString();
    }

    public List<String> getRawCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getFileExtension(){
        return file.getFileName().toString().split("\\.")[1].toLowerCase();
    }

    @Override
    public String toString() {
        return "Product{" +
                "SKU='" + SKU + '\'' +
                ", weight='" + weight + '\'' +
                ", name='" + getName() + '\'' +
                ", categories=" + categories +
                ", file=" + file +
                '}';
    }

    public String lessInfo() {
        return "Product{" +
                "SKU='" + SKU + '\'' +
                ", weight='" + weight + '\'' +
                ", file=" + file +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return SKU.equals(product.SKU);
    }

    @Override
    public int hashCode() {
        return Objects.hash(SKU);
    }
}
