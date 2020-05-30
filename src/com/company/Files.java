package com.company;

import Crypto.Aes256;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.company.User.findLogin;

public class Files implements Serializable {
    private static final String AdminAccount = "D:\\4 сем\\kursach\\files\\Admin";
    public static final String UserNumber = "D:\\4 сем\\kursach\\files\\UserNumber";
    public static final String UserAccount = "D:\\4 сем\\kursach\\files\\UserAccount";
    private static final String AdminSecretKey="D:\\4 сем\\kursach\\files\\AdminAes256Class";
    private static final String UsersSecretKey="D:\\4 сем\\kursach\\files\\UserAes256Class";
    public static final String ApplicationInformation ="D:\\4 сем\\kursach\\files\\ApplicationInformation";
    public static final String MethodInformation ="D:\\4 сем\\kursach\\files\\MethodInformation";
    public static final String Answers ="D:\\4 сем\\kursach\\files\\Answers";
    public static final String Report ="D:\\4 сем\\kursach\\files\\Report";
    private int userNumber;
    Aes256 aes256 = new Aes256();
    public int  fileLength;
    public void setUserNumber(int userNumber){this.userNumber=userNumber;}
    public  int getUserNumber() { return userNumber; }

    public void readSecretKey(String way) throws IOException, NoSuchAlgorithmException {
        File fileName=new File(way);
        if(!fileName.exists() || fileName.length()==0) {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            aes256.setSecretKey(keyGenerator.generateKey());
        }
        else{
            FileReader fr= new FileReader(fileName);
            Scanner scan = new Scanner(fr);
            byte[] decodedKey = Base64.getDecoder().decode(scan.nextLine());
            aes256.setSecretKey(new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"));
            fr.close();
    }}

    public void writeAdminSecretKey() throws IOException, NoSuchAlgorithmException {
        aes256.generateNewKey();
        File fileName=new File(AdminSecretKey);
            FileWriter fr= new FileWriter(fileName);
        fr.write(aes256.getSecretKey());
            fr.close();
        }

    public void writeUsersSecretKey() throws IOException, NoSuchAlgorithmException {
        aes256.generateNewKey();
        File fileName=new File(UsersSecretKey);
        FileWriter fr= new FileWriter(fileName);
        fr.write(aes256.getSecretKey());
        fr.close();
    }

    public Admin readAdminAccount() {
        try {
            File file = new File(AdminAccount);
            if (!file.exists()) {
                FileOutputStream oos = new FileOutputStream(file);
                oos.close();
                return new Admin();
            }
            if (file.length() == 0) return new Admin();
            fileLength = 1;
            this.readSecretKey(AdminSecretKey);
            FileInputStream adminFileReader = new FileInputStream(file);
            Admin admin = new Admin();
            byte[] src = new byte[adminFileReader.available()];
            String str;
            int k = -1, j = 0;
            while ((k = adminFileReader.read()) != -1) {
                src[j] = (byte) k;
                j++;
            }
            src = aes256.makeAes(src, Cipher.DECRYPT_MODE);
            str = new String(src);
            Scanner scanner= new Scanner(str);
                admin.getAccount().setLogin(scanner.nextLine());
                admin.getAccount().setPassword(scanner.nextLine());
            adminFileReader.close();
            return admin;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Admin();
        }
    }

        public void readUserNumber() throws Exception {
        File fileName=new File(UserNumber);
        if(!fileName.exists() || fileName.length()==0)
            setUserNumber(0);
        else{
        FileReader number= new FileReader(fileName);
        Scanner scan = new Scanner(number);
      setUserNumber(Integer.parseInt(scan.nextLine()));
        number.close();
    } }

    public void writeUserNumber() throws Exception {
        File fileName=new File(UserNumber);
        if(!fileName.exists())
            setUserNumber(1);
            FileWriter fr= new FileWriter(fileName,false);
            fr.write(Integer.toString(getUserNumber()));
            fr.close();
        }

    public List<User> readUserAccount(){
        try{
            this.readUserNumber();
            if(this.getUserNumber()==0){
                FileOutputStream oos = new FileOutputStream(new File(UserAccount));
                oos.close();
                return new ArrayList<User>();
            }
            this.readSecretKey(UsersSecretKey);
            FileInputStream usersFileReader = new FileInputStream(new File(UserAccount));
            List<User> users = new ArrayList();
            byte[] src = new byte[usersFileReader.available()];
            String str;
            int k = -1, j = 0;
            while ((k = usersFileReader.read()) != -1) {
                src[j] = (byte) k;
                j++;
            }
            src = aes256.makeAes(src, Cipher.DECRYPT_MODE);
            str = new String(src);
            Scanner scanner= new Scanner(str);
            for(int i=0;i<getUserNumber();i++) {
                User user=new User();
            user.setName(scanner.nextLine());
            user.setSurname(scanner.nextLine());
            user.getAccount().setLogin(scanner.nextLine());
            user.getAccount().setPassword(scanner.nextLine());
            users.add(user);
            }
            return users;
        }catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void writeAdminAccount(Admin admin) {
        try{
            this.writeAdminSecretKey();
            byte[] loginPassword = aes256.makeAes(admin.getAccount().getLoginPasswordBytes(), Cipher.ENCRYPT_MODE);
            File file=new File(AdminAccount);
            FileOutputStream adminFileWriter=new FileOutputStream(file);
            adminFileWriter.write(loginPassword);
            adminFileWriter.close();
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

        }

    public void writeUserAccount(List<User> newUsers) throws Exception{
        try{
            List<User>users;
            users=readUserAccount();
            users.addAll(newUsers);
            setUserNumber(users.size());
            writeUsersSecretKey();
            String str="";
            for(int i=0;i<users.size();i++){
            str+=users.get(i).getUserData();
            }
            byte[] usersData = aes256.makeAes(str.getBytes(), Cipher.ENCRYPT_MODE);
            File file=new File(UserAccount);
            FileOutputStream userFileWriter=new FileOutputStream(file);
            userFileWriter.write(usersData);
            writeUserNumber();
            userFileWriter.close();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void clearFile(String fileName) throws IOException {
        FileWriter fileWriter=new FileWriter(fileName);
        fileWriter.write("");
        fileWriter.close();
    }
    public void writeApplication(List<Application> newApplications) throws Exception {
        List<Application> applications = readApplications();
        readUserNumber();
        List<User>users=readUserAccount();
        for(Application application:newApplications)
        application.getUser().getAccount().setLogin(findLogin(users,application.getUser().getSurname()));
        applications.addAll(newApplications);
        File fileName=new File(ApplicationInformation);
        FileWriter fr= new FileWriter(fileName,false);
        for(Application application1 : applications){
            fr.write(application1.getUser().getName()+"\n");
            fr.write(application1.getUser().getSurname()+"\n");
            fr.write(application1.getUser().getAccount().getLogin()+"\n");
            fr.write(application1.getUser().getCar().getYearRelease()+"\n");
            fr.write(application1.getUser().getCar().getCarModel()+"\n");
            fr.write(application1.getUser().getCar().getCarBrand()+"\n");
            fr.write(application1.getUser().getCar().getVinNumber()+"\n");
            fr.write(application1.getUser().getCar().getRegistrationNumber()+"\n");
        }
        fr.close();
    }

    public List<Application> readApplications() throws IOException {

        File fileName=new File(ApplicationInformation);
        if(!fileName.exists() || fileName.length()==0) {
            return new ArrayList();
        }
        else{
            List<Application> applications =new ArrayList();

            FileReader readApplication= new FileReader(fileName);
            Scanner scan = new Scanner(readApplication);
            while(scan.hasNextLine()){
                Application application =new Application();
                Car car=new Car();
                User user=new User();
                user.setName(scan.nextLine());
                user.setSurname(scan.nextLine());
                user.getAccount().setLogin(scan.nextLine());
                car.setYearRelease(scan.nextLine());
                car.setCarModel(scan.nextLine());
                car.setCarBrand(scan.nextLine());
                car.setVinNumber(scan.nextLine());
                car.setRegistrationNumber(scan.nextLine());
                user.setCar(car);
                application.setUser(user);
                applications.add(application);
            }
            readApplication.close();
            return applications;
        }
    }
    public void writeMethodInformation(String methodInformation) throws IOException {
        File fileName=new File(MethodInformation);
        FileWriter fr= new FileWriter(fileName,true);
        fr.write(methodInformation);
        fr.close();
    }

    public MethodExpertEvaluation readMethodInformation() throws IOException {
        File fileName=new File(MethodInformation);
        FileReader readMethodInformation=new FileReader(fileName);
        Scanner scanner=new Scanner(readMethodInformation);
        MethodExpertEvaluation methodInformation=new MethodExpertEvaluation();
        methodInformation.setServiceNumber(Integer.parseInt(scanner.nextLine()));
        int [][]mass=new int[methodInformation.getServiceNumber()][5];
        for(int i=0;i< methodInformation.getServiceNumber();i++) {

            scanner.nextLine();
            for (int j = 0; j <5; j++) {
                mass[i][j] = Integer.parseInt(scanner.nextLine());
            }
            scanner.nextLine();
        }
            methodInformation.setServiceMatrix(mass);
        mass=new int[5][5];
        for(int i=0;i<5;i++)
            for (int j = 0; j < 5; j++) {
                mass[i][j] = Integer.parseInt(scanner.nextLine());
            }
                methodInformation.setExpertMatrix(mass);
return methodInformation;
    }

    public String readService() throws IOException {
        String services="";
        File fileName=new File(MethodInformation);
        FileReader readMethodInformation=new FileReader(fileName);
        Scanner scanner=new Scanner(readMethodInformation);
       int number=Integer.parseInt(scanner.nextLine());
        for(int i=1;i<=number;i++){
            services+=scanner.nextLine()+"\n";
            scanner.nextLine();
            scanner.nextLine();
            scanner.nextLine();
            scanner.nextLine();
            scanner.nextLine();
            scanner.nextLine();
        }
        scanner.close();
        readMethodInformation.close();
        return services;
    }
    public String readPrice() throws IOException {
        String prices="";
        File fileName=new File(MethodInformation);
        FileReader readMethodInformation=new FileReader(fileName);
        Scanner scanner=new Scanner(readMethodInformation);
        int number=Integer.parseInt(scanner.nextLine());
        for(int i=1;i<=number;i++){
            scanner.nextLine();
            scanner.nextLine();
            scanner.nextLine();
            scanner.nextLine();
            scanner.nextLine();
            scanner.nextLine();
            prices+=scanner.nextLine()+"\n";
        }
        scanner.close();
        readMethodInformation.close();
        return prices;
    }
    public int getMethodInformationSize() {
        File fileName=new File(MethodInformation);
        if(!fileName.exists())return 1;
        if(fileName.length()!=0)return 2;
        else return 1;
    }
    public String findApplicationLogin() throws IOException {
        File fileName=new File(MethodInformation);
        FileReader readMethodInformation=new FileReader(fileName);
        Scanner scanner=new Scanner(readMethodInformation);
        String str="";
        while(scanner.hasNextLine()) {
            str = scanner.nextLine();
            if (!scanner.hasNextLine()) {
                scanner.close();
                readMethodInformation.close();
                return str;
            }
        }
        return str;
    }
    public void writeAnswerUser(Answer answer) throws IOException {
        File fileName=new File(Answers);
        FileWriter writeAnswer = new FileWriter(fileName,true);
        writeAnswer.write(answer.getAccount().getLogin()+"\n");
        writeAnswer.write(answer.getService()+"\n");
        writeAnswer.write(answer.getPrice()+"\n");
        Date date=new Date();
        writeAnswer.write(date.toString()+"\n");
        writeAnswer.close();
    }
    public List<Answer> readAnswerUser() throws FileNotFoundException {
        List<Answer>answers=new ArrayList();
        File fileName=new File(Answers);
        if(!fileName.exists()|| fileName.length()==0)
            return new ArrayList();
        FileReader fileReader=new FileReader(fileName);
        Scanner scanner=new Scanner(fileReader);
        while(scanner.hasNextLine()){
            Answer answer=new Answer();
            Account account=new Account();
            account.setLogin(scanner.nextLine());
            answer.setAccount(account);
            answer.setService(scanner.nextLine());
            answer.setPrice(scanner.nextLine());
            answer.setTime(scanner.nextLine());
            answers.add(answer);
        }
        return answers;
    }
    public void report(String allServices,String best) throws IOException {
        File fileName=new File(Report);
        FileWriter writeReport = new FileWriter(fileName);
        Scanner scanner=new Scanner(allServices);
        Date date=new Date();
        writeReport.write("Отчет о результатах "+date.toString()+"\n");
        writeReport.write("Первонеобходимая услуга: "+best+"\n");
        writeReport.write("рассматривались альтернативы:"+"\n");
        String str="";
        while(scanner.hasNextLine()) {
            str=scanner.nextLine();
            if(!str.equals(best))
            writeReport.write(str + "\n");
        }
        writeReport.close();

    }
}

