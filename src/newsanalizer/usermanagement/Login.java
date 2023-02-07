package newsanalizer.usermanagement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Login {

    public void doLogin() throws InterruptedException {

        // load from db the list of users
        List<User> listUsers = loadDB();
//        for(User u: listUsers)
//            System.out.println(u);

        // read from kb a user
        //while user from kb ! = a user in db stay here

        int MAXTRIES = 3;
        int failLogin = 0;
        boolean success = false;

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
                }
            }

            if (failLogin == MAXTRIES) {
                System.out.println("You failed to enter your account to many times!");
                System.out.println("Please wait 5 seconds and try again.");
                System.out.println("5");
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
                failLogin = 0;
            }

        }
        while (!success);
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
                    String admin = st.nextToken();


                    uObj.setUsername(u.trim());
                    uObj.setPassword(p.trim());
                }
                listOfUsers.add(uObj);
            }

        return listOfUsers;
    }


}
