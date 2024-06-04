class Product {
    int id, amount, minAmount;
    String name, brand, type, size, color, description;
    double price;

    public Product(int id, int amount, int minAmount, String name, String brand, String type, String size, String color, String description, double price) {
        this.id = id;
        this.amount = amount;
        this.minAmount = minAmount;
        this.name = name;
        this.brand = brand;
        this.type = type;
        this.size = size;
        this.color = color;
        this.description = description;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getType() {
        return type;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }
}