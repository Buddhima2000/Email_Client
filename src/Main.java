// 200151P
//import libraries
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
class  Email_Client {
    public static ArrayList<SendEmailTLS> SentMails = new ArrayList<>();
    static LocalDate day = LocalDate.now();
    public static void main(String[] args) {
        Client_Manager cl = new Client_Manager();
        SentMails = Client_Manager.Deserielize(); //deserializing arraylist that have emails history
        FileReadAndWrite.ReadFile("clientList.txt"); //reading recipient details from the file
        System.out.println("\n....Welcome to the Email Client....\n");
        SendBirthdayWishes(day, SentMails); //sending wishes who have birthday today
        Scanner scanner = new Scanner(System.in);
        boolean start = true;
        while(start){
            System.out.println("Enter option type: \n"
                    + "1 - Adding a new recipient\n"
                    + "2 - Sending an email\n"
                    + "3 - Printing out all the recipients who have birthdays\n"
                    + "4 - Printing out details of all the emails sent\n"
                    + "5 - Printing out the number of recipient objects in the application\n"
                    + "6 - Exit from email Client");
            int option = scanner.nextInt();
            Client_Manager.Serialize();
            SentMails = Client_Manager.Deserielize();
            switch (option) {
                case 1:
                    // input format - Official: nimal,nimal@gmail.com,ceo
                    System.out.println("Adding a new recipient");
                    System.out.println("Input Format --->");
                    System.out.println("Official: nimal,nimal@gmail.com,ceo");
                    System.out.println("Office_friend: kamal,kamal@gmail.com,clerk,2000/12/12");
                    System.out.println("Personal: sunil,<nick-name>,sunil@gmail.com,2000/10/10");
                    Scanner clientInput = new Scanner(System.in);
                    String[] recipient_details = clientInput.nextLine().split(": ");
                    FileReadAndWrite.WriteFile("clientList.txt",recipient_details[0],recipient_details[1]);
                    cl.add_recipient_details(recipient_details[0], recipient_details[1].split(","));
                    break;
                case 2:
                    // input format - email, subject, content
                    // code to send an email
                    System.out.println("Sending an email");
                    System.out.println("input format - email,subject,content");
                    scanner.nextLine();
                    String[] input = scanner.nextLine().split(",");
                    SendEmailTLS email = new SendEmailTLS(input[0], input[1], input[2]);
                    email.SendEmail();
                    SentMails.add(email);
                    break;
                case 3:
                    // input format - yyyy/MM/dd (ex: 2018/09/17)
                    // code to print recipients who have birthdays on the given date
                {
                    System.out.println("input format - yyyy/MM/dd (ex: 2018/09/17)");
                    //formatting string date to LocalDate object
                    LocalDate inputDate = LocalDate.parse(scanner.next(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                    BirthdayManage.printBirthdayName(inputDate);
                    System.out.println("\n");
                }
                break;
                case 4:
                    // input format - yyyy/MM/dd (ex: 2018/09/17)
                    // code to print the details of all the emails sent on the input date
                    System.out.println("input format - yyyy/MM/dd (ex: 2018/09/17)");
                    String givenDate = scanner.next();
                    for(SendEmailTLS i: SentMails){
                        if(i.getSentDate().equals(givenDate)){
                            System.out.println("sent email: " +i.getToEmail());
                            System.out.println("subject : " +i.getSubject());
                            System.out.println("content : " +i.getContent());
                            System.out.println("date : " +i.getSentDate());
                            System.out.println("\n");
                        }
                    }
                    break;
                case 5:
                    // code to print the number of recipient objects in the application
                    System.out.println("Number of Recipients in the application:");
                    System.out.println(Recipient.count);
                    System.out.println("\n");
                    break;
                case 6:
                    System.out.println("Exit from the Email Client...");
                    start=false; //terminating the while loop to exit
                    break;
            }
        }
    }
    public static void SendBirthdayWishes(LocalDate today, ArrayList<SendEmailTLS> prev_emails) {
        if(prev_emails.size()== 0 || !prev_emails.get(prev_emails.size()-
                1).getSentDate().equals(formatDate(Email_Client.day))){
            System.out.println("sending wishes:");
            for (WishingRecipient rec : Client_Manager.wishingRec) {
                if (rec.getBirthday().substring(5).equals(formatDate(today).substring(5))) {
                    SendEmailTLS sent=rec.birthdayWish();
                    SentMails.add(sent);
                }
            }
        }
    }
    public static String formatDate(LocalDate date) { //format the date into relevant format
        return date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
}
class Client_Manager {
    public static ArrayList<WishingRecipient> wishingRec = new ArrayList<>();
    static ArrayList<Recipient> recipientList = new ArrayList<>();
    public static void Serialize() {
        try {
            FileOutputStream fileStream = new FileOutputStream("objects.txt");
            ObjectOutputStream os = new ObjectOutputStream(fileStream);
            os.writeObject(Email_Client.SentMails);
            fileStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static ArrayList<SendEmailTLS> Deserielize() {
        ArrayList<SendEmailTLS> prevoius_emails = new ArrayList<SendEmailTLS>();
        try {
            File des = new File("objects.txt");
            if(des.length() !=0){
                FileInputStream fileStream = new FileInputStream(des);
                ObjectInputStream os = new ObjectInputStream(fileStream);
                prevoius_emails = (ArrayList<SendEmailTLS>) os.readObject();
                os.close();
                fileStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException c){
            c.printStackTrace();
        }
        return prevoius_emails;
    }
    public void add_recipient_details(String recipient_type, String[] details) { //creating recipient objects
        switch (recipient_type) {
            case "Official":
                Recipient Off_recipent = new OfficialRecipients(details[0], details[1], details[2]);
                recipientList.add(Off_recipent);
                break;
            case "Office_friend":
                Recipient Off_friend = new OfficialFriends(details[0], details[1], details[2], details[3]);
                recipientList.add(Off_friend);
                wishingRec.add((WishingRecipient) Off_friend);
                break;
            case "Personal":
                Recipient personal_friend = new PersonalRecipients(details[0], details[1], details[2], details[3]);
                recipientList.add(personal_friend);
                wishingRec.add((WishingRecipient) personal_friend);
                break;
        }
    }
}
class BirthdayManage {
    public static void printBirthdayName(LocalDate inputDate) { //find the names of given birth day
        System.out.println("print recipients who have birthdays on the given date");
        for (WishingRecipient rec : Client_Manager.wishingRec) {
            if (rec.getBirthday().substring(5).equals(formatDate(inputDate).substring(5))) {
                System.out.println(((Recipient) rec).getName());
            }
        }
    }
    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
}
class SendEmailTLS implements Serializable {
    private String toEmail;
    private String subject;
    private String content;
    private String sentDate;
    public SendEmailTLS(String toEmail, String subject, String content) {
        this.toEmail = toEmail;
        this.subject = subject;
        this.content = content;
        this.sentDate= Email_Client.formatDate(Email_Client.day);
    }
    final String username = "buddhimacse@gmail.com";
    final String password = "pwnwrgcvdrujfhav";
    public void SendEmail() {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("buddhimacse@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );
            message.setSubject(subject);
            message.setText(content);
            Transport.send(message);
            System.out.println("Email Sent");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    public String getToEmail(){return toEmail;}
    public String getSubject(){return subject;}
    public String getContent(){return content;}
    public String getSentDate(){return sentDate;}
}
class FileReadAndWrite {
    // store details in clientList.txt file
    public static void WriteFile(String fileName, String type, String Details) {
        try {
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(type + ": " + Details + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void ReadFile(String fileName) {
        Client_Manager cl = new Client_Manager();
        try {
            new File("clientList.txt").createNewFile();
            File clientList = new File("clientList.txt");
            Scanner fileReader = new Scanner(clientList); //reading the clientList file
            while (fileReader.hasNextLine()) {
                String data = fileReader.nextLine();
                String[] splits = data.split(":");
                cl.add_recipient_details(splits[0], splits[1].strip().split(",")); //adding recipient to the
                //arraylist
            }
            fileReader.close();
        } catch (
                FileNotFoundException e) {
            System.out.println("There is an Error in your file!");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
abstract class Recipient implements Serializable {
    private String Name;
    protected String Email;
    public static int count=0;
    public Recipient(String name, String email) {
        Name = name;
        Email = email;
        count++;
    }
    public String getName() {
        return Name;
    }
    public String getEmail() {
        return Email;
    }
}
class OfficialRecipients extends OfficialClient{
    public OfficialRecipients(String Name, String Email, String Designation) {
        super(Name, Email, Designation);
    }
}
class OfficialClient extends Recipient implements Serializable {
    private String Designation;
    public OfficialClient(String Name, String Email, String Designation) {
        super(Name, Email);
        this.Designation=Designation;
    }
}
class OfficialFriends extends OfficialClient implements WishingRecipient{
    String BirthDay;
    public OfficialFriends(String Name, String Email, String Designation, String BirthDay) {
        super(Name, Email, Designation);
        this.BirthDay= BirthDay;
    }
    @Override
    public SendEmailTLS birthdayWish() {
        SendEmailTLS email = new SendEmailTLS(super.Email,"Happy Birthday",
                "Wish you a Happy Birthday. Buddhima");
        email.SendEmail();
        return email ;
    }
    @Override
    public String getBirthday() {
        return this.BirthDay;
    }
}
class PersonalRecipients extends Recipient implements WishingRecipient{
    String BirthDay;
    String NickName;
    public PersonalRecipients(String Name, String NickName, String Email, String BirthDay) {
        super(Name, Email);
        this.BirthDay=BirthDay;
        this.NickName=NickName;
    }
    @Override
    public SendEmailTLS birthdayWish() {
        SendEmailTLS email = new SendEmailTLS(super.Email,
                "Happy Birthday", "hugs and love on your birthday. Buddhima");
        email.SendEmail();
        return email ;
    }
    @Override
    public String getBirthday() {
        return this.BirthDay;
    }
}
interface WishingRecipient {
    SendEmailTLS birthdayWish();
    String getBirthday();
}