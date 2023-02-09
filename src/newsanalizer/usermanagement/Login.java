package newsanalizer.usermanagement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;

public class Login {

    private static List<User> listUsers;

    public void doLogin() {

        // load from db the list of users
        listUsers = loadDB();
//        for(User u: listUsers)
//            System.out.println(u);

        // read from kb a user
        //while user from kb ! = a user in db stay here

        int MAXTRIES = 3;
        int failLogin = 0;
        boolean success = false;
        boolean isAdmin = false;

        do {

            System.out.println("Username:");
            String kbUsername = new Scanner(System.in).nextLine();
            System.out.println("Password:");
            String kbPwd = new Scanner(System.in).nextLine();

            User userKb = new User();
            userKb.setUsername(kbUsername);
            userKb.setPassword(kbPwd);

            failLogin++;

            for (User u : listUsers) {

                if (u.equalsUsers(userKb)) {
                    System.out.println("You logged in successfully");
                    success = true;
                    if (u.isAdmin())
                        isAdmin = true;
                }
            }

            if (failLogin == MAXTRIES) {
                System.out.println("You failed to enter your account to many times!");
                System.out.println("Please wait 5 seconds and try again.");
                System.out.println("5");
                try {
                    Thread.sleep(1000);
                    System.out.println("4");
                    Thread.sleep(1000);
                    System.out.println("3");
                    Thread.sleep(1000);
                    System.out.println("2");
                    Thread.sleep(1000);
                    System.out.println("1");
                    Thread.sleep(1000);
                    System.out.println("0");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                failLogin = 0;
            }

        }
        while (!success);

        if (isAdmin)
            printAdminMenu();
        else
            printAnalystMenu();
    }

    private List<User> loadDB() {

        Path path = Paths.get("users.txt");
        List<User> listOfUsers = new ArrayList<>();

        List<String> listOfUsersAsStrings = null;
        try {
            listOfUsersAsStrings = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(listOfUsersAsStrings);

        for (int i = 0; i < listOfUsersAsStrings.size(); i++) {
            User uObj = new User();
            String currentLineOfText = listOfUsersAsStrings.get(i);
            StringTokenizer st = new StringTokenizer(currentLineOfText, ",");
            while (st.hasMoreTokens()) {
                String u = st.nextToken();
                String p = st.nextToken();
                String admin = st.nextToken().trim();


                uObj.setUsername(u.trim());
                uObj.setPassword(p.trim());
                uObj.setAdmin(Boolean.parseBoolean(admin));
            }
            listOfUsers.add(uObj);
        }

        return listOfUsers;
    }

    private static void printAdminMenu() {
        System.out.println("0. Add user.");
        String option = new Scanner(System.in).nextLine();
        if (option.equalsIgnoreCase("0")) {

            System.out.println("New Username:");
            String kbUsername = new Scanner(System.in).nextLine();
            System.out.println("New Password:");
            String kbPwd = new Scanner(System.in).nextLine();

            User userKb = new User();
            userKb.setUsername(kbUsername);
            userKb.setPassword(kbPwd);
            listUsers.add(userKb);
            String newRow = "\n" + kbUsername + "," + kbPwd + ",false";
            Path pOut = Paths.get("newusers.txt");
            try {
                Files.write(pOut, newRow.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void printAnalystMenu() {
        System.out.println("1. Upload news and analyze ");
        String option = new Scanner(System.in).nextLine();
        if (option.equalsIgnoreCase("1")) {
            System.out.println("Upload news filename: ");
            String filename = new Scanner(System.in).nextLine();

            analyzeNews(filename);

        }

    }

    public static void analyzeNews(String filename) {

        String news = null;
        Path p = Paths.get(filename);
        try {
            news = new String(Files.readAllBytes(p));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringTokenizer st = new StringTokenizer(news, "~");
        String currentNews;
        while (st.hasMoreTokens()) {
            currentNews = st.nextToken().toLowerCase().trim();

            Map<String, Integer> report = parseNews(currentNews);

            String newsName = "newsanalyzer" + System.currentTimeMillis() + ".txt";
            Path pOut = Paths.get(newsName);

            StringBuilder row = new StringBuilder();
            for (Map.Entry<String, Integer> value : report.entrySet()) {
                row.append(value.getKey()).append(" : ").append(value.getValue()).append("\n");

                try {
                    Files.write(pOut, row.toString().getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }

    }

    private static Map parseNews(String currentNews) {
        Map<String, Integer> report = new HashMap<>();

        StringTokenizer stCurrentNews = new StringTokenizer(currentNews);
        int nrWordsPerCurrentNews = 0;
        while (stCurrentNews.hasMoreTokens()) {
            String tokenNews = stCurrentNews.nextToken();

            int index = tokenNews.lastIndexOf(".");
            if (index != -1)
                tokenNews = tokenNews.substring(0, index);

            index = tokenNews.lastIndexOf(",");
            if (index != -1)
                tokenNews = tokenNews.substring(0, index);

            index = tokenNews.lastIndexOf("?");
            if (index != -1)
                tokenNews = tokenNews.substring(0, index);

            index = tokenNews.lastIndexOf("!");
            if (index != -1)
                tokenNews = tokenNews.substring(0, index);


            if (tokenNews.length() > 4) {
                if (!report.containsKey(tokenNews))
                    report.put(tokenNews, 1);
                else
                    report.put(tokenNews, report.get(tokenNews) + 1);
            }
        }
        return report;
    }
}
