import java.util.*;

class Item {
    int id;
    String name;
    String category;
    double price;
    int availableQuantity;

    public Item(int id, String name, String category, double price, int availableQuantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }
}

class Customer {
    int id;
    String username;
    String password; 
    String role;

    public Customer(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}

class OrderItem {
    Item item;
    int quantity;
    double cost;

    public OrderItem(Item item, int quantity, double cost) {
        this.item = item;
        this.quantity = quantity;
        this.cost = cost;
    }
}

class Order {
    int customerId;
    List<OrderItem> items;
    double totalAmount;

    public Order(int customerId, List<OrderItem> items, double totalAmount) {
        this.customerId = customerId;
        this.items = items;
        this.totalAmount = totalAmount;
    }
}

public class OrderManagementSystem {
    static Map<Integer, Customer> customers = new HashMap<>();
    static Map<Integer, Item> items = new HashMap<>();
    static Map<Integer, List<Order>> orderHistory = new HashMap<>();

    public static void main(String[] args) {
        initializeData();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Order Management System ---");
            System.out.print("Enter Customer ID: ");
            int customerId = sc.nextInt();
            sc.nextLine(); 
            System.out.print("Enter Password: ");
            String password = sc.nextLine();

            if (authenticate(customerId, password)) {
                Customer customer = customers.get(customerId);
                if (customer.role.equals("Admin")) {
                    adminMenu(customer);
                } else {
                    customerMenu(customer);
                }
            } else {
                System.out.println("Invalid credentials. Please try again.");
            }
        }
    }

    public static void initializeData() {
        customers.put(100, new Customer(100, "admin", encryptPassword("adminpass"), "Admin"));
        customers.put(101, new Customer(101, "Aldous", encryptPassword("aldous123"), "Customer"));
        customers.put(102, new Customer(102, "roy", encryptPassword("roy123"), "Customer"));
        customers.put(103, new Customer(103, "john", encryptPassword("john123"), "Customer"));

        items.put(1, new Item(1, "Dove Conditioner", "Conditioner", 25, 10));
        items.put(2, new Item(2, "Pantene Conditioner", "Conditioner", 30, 10));
        items.put(4, new Item(4, "Lux Soap", "Soap", 15, 10));
        items.put(5, new Item(5, "Dove Soap", "Soap", 30, 5));
    }

    public static String encryptPassword(String password) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : password.toCharArray()) {
            encrypted.append((char) (c + 1));
        }
        return encrypted.toString();
    }

    public static boolean authenticate(int customerId, String password) {
        if (customers.containsKey(customerId)) {
            Customer customer = customers.get(customerId);
            return customer.password.equals(encryptPassword(password));
        }
        return false;
    }

    public static void customerMenu(Customer customer) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. Place an Order");
            System.out.println("2. View Order History");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    placeOrder(customer.id);
                    break;
                case 2:
                    viewOrderHistory(customer.id);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    public static void adminMenu(Customer admin) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Update Item");
            System.out.println("2. Add New Customer");
            System.out.println("3. View Low Stock Items");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    updateItem();
                    break;
                case 2:
                    addNewCustomer();
                    break;
                case 3:
                    viewLowStockItems();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    public static void placeOrder(int customerId) {
        Scanner sc = new Scanner(System.in);
        List<OrderItem> cart = new ArrayList<>();
        double totalAmount = 0;

        System.out.println("Available Items:");
        for (Item item : items.values()) {
            System.out.println(item.id + ": " + item.name + " - $" + item.price 
                + " (Stock: " + item.availableQuantity + ")");
        }

        while (true) {
            System.out.print("Enter Item ID to add to cart (or 0 to finish): ");
            int itemId = sc.nextInt();
            if (itemId == 0) break;

            if (!items.containsKey(itemId)) {
                System.out.println("Invalid Item ID.");
                continue;
            }

            Item item = items.get(itemId);
            System.out.print("Enter quantity: ");
            int quantity = sc.nextInt();

            if (quantity > item.availableQuantity) {
                System.out.println("Insufficient stock available.");
                continue;
            }

            double itemCost = item.price * quantity;
            totalAmount += itemCost;

            cart.add(new OrderItem(item, quantity, itemCost));
            item.availableQuantity -= quantity;
        }

        System.out.println("Total Amount: $" + totalAmount);
        System.out.print("Confirm order? (yes/no): ");
        String confirm = sc.next();

        if (confirm.equalsIgnoreCase("yes")) {
            orderHistory.computeIfAbsent(customerId, k -> new ArrayList<>())
                .add(new Order(customerId, cart, totalAmount));
            System.out.println("Order placed successfully!");
        } else {
            System.out.println("Order cancelled.");
            for (OrderItem orderItem : cart) {
                orderItem.item.availableQuantity += orderItem.quantity;
            }
        }
    }

    public static void viewOrderHistory(int customerId) {
        List<Order> orders = orderHistory.get(customerId);
        if (orders == null || orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        System.out.println("--- Order History ---");
        for (Order order : orders) {
            System.out.println("Order Total: $" + order.totalAmount);
            for (OrderItem item : order.items) {
                System.out.println(item.item.name + " x" + item.quantity + " - $" + item.cost);
            }
            System.out.println();
        }
    }

    public static void updateItem() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Item ID to update: ");
        int itemId = sc.nextInt();

        if (!items.containsKey(itemId)) {
            System.out.println("Invalid Item ID.");
            return;
        }

        Item item = items.get(itemId);
        System.out.print("Enter new quantity: ");
        int newQuantity = sc.nextInt();
        item.availableQuantity = newQuantity;

        System.out.print("Enter new price (or -1 to keep current price): ");
        double newPrice = sc.nextDouble();
        if (newPrice != -1) {
            item.price = newPrice;
        }

        System.out.println("Item updated successfully!");
    }

    public static void addNewCustomer() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter new customer's username: ");
        String username = sc.nextLine();
        System.out.print("Enter new customer's password: ");
        String password = sc.nextLine();
        System.out.print("Confirm password: ");
        String confirmPassword = sc.nextLine();

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Try again.");
            return;
        }

        int newId = customers.keySet().stream().max(Integer::compareTo).orElse(100) + 1;
        customers.put(newId, new Customer(newId, username, encryptPassword(password), "Customer"));
        System.out.println("New customer added successfully with ID: " + newId);
    }

    public static void viewLowStockItems() {
        System.out.println("--- Low Stock Items ---");
        for (Item item : items.values()) {
            if (item.availableQuantity < 3) {
                System.out.println(item.id + ": " + item.name + " - Stock: " + item.availableQuantity);
            }
        }
    }
}