import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


import java.io.*;
import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;
import java.util.OptionalInt;

public class ExpenseTracker {
    private static final String FILE_NAME = "expenses.json";
    private static final Set<Integer> ids = new HashSet<>();
    private static final SecureRandom random = new SecureRandom();
    private static final Gson gson = new Gson();
    //private LocalDate date;
    public static void main(String[] args) {
        System.out.println("Salam!");
        System.out.print("Expense Tracker $ ");
        Scanner scan = new Scanner(System.in);
        while(true) {
            String action =  " ";
            String description = " ";
            String amount = " ";
            String[] command = scan.nextLine().split(" ",3);
            if(command.length > 2) {
                action = command[0];
                description = command[1];
                amount = command[2];
            }
            else if (command.length == 1){
                action = command[0];
            }



            switch(action){
                case "add" ->{
                    try {
                        addExpense(Double.parseDouble(amount), description);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                case "remove" -> {
                    try {
                        removeExpense(description);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case "update" -> {
                    try {
                        updateExpense(description, Double.parseDouble(amount));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case "list" -> {
                    try {
                        listExpenses();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
    public static void addExpense(double amount, String description) throws IOException{
        //METHOD to add expense
        List<JsonObject> expenses = loadExpenses();
        JsonObject expense = new JsonObject();
        expense.addProperty("description", description);
        expense.addProperty("amount", amount);
        expense.addProperty("id",generateID());
        expense.addProperty("createdAt",LocalDate.now().toString());
        expense.addProperty("updatedAt",LocalDate.now().toString());
        expenses.add(expense);
        saveExpenses(expenses);
        System.out.println("Expenses added successfully");


    }
    public static void removeExpense(String id) throws IOException{
        //METHOD to remove expense
        List<JsonObject> expenses = loadExpenses();

        // code to remove expenses
        expenses.removeIf( expense ->
            expense.getAsJsonPrimitive("id").getAsInt() == Integer.parseInt(id)
        );
        saveExpenses(expenses);
        System.out.println("Removed expense with ID "+id);
    }
    public static void updateExpense(String desc, double amount) throws IOException{
        //METHOD to update the expense
        List<JsonObject> expenses = loadExpenses();
        expenses.forEach(expense -> {
            if( expense.getAsJsonPrimitive("description").getAsString() == desc){
                expense.addProperty("amount",amount);
                expense.addProperty("updatedAt",LocalDate.now().toString());
                try{
                    saveExpenses(expenses);
                } catch(Exception e) {
                    System.out.println("cannot update expense");
                }
            }
        });
    }
    public static void listExpenses() throws IOException {
        //METHOD to list expenses
        List<JsonObject> expenses = loadExpenses();
        System.out.println("All expenses: ");
        expenses.forEach(System.out::println);
    }
    public static int generateID(){
        int id;
        do {
            id = random.nextInt(10000)+9999;
        } while(ids.contains(id));
        return id;
    }
    public static List<JsonObject> loadExpenses() throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            file.createNewFile();
            try (Writer writer = new FileWriter(file)) {
                writer.write("[]");  // Initialize with an empty JSON array
            }
        }
        try (Reader reader = new FileReader(FILE_NAME)) {
            Type listType = new TypeToken<ArrayList<JsonObject>>(){}.getType();

            return gson.fromJson(reader, listType);
        }
    }
    public static void saveExpenses(List<JsonObject> expenses) throws IOException {
        try (Writer writer = new FileWriter(FILE_NAME)){
            gson.toJson(expenses, writer);
        }
    }

}