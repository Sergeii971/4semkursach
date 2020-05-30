package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.company.Files.*;

class MonoThreadClientHandler implements Runnable {
    private static Socket clientDialog;
    DataOutputStream out;
    DataInputStream number;
    DataInputStream in;
    private Files files = new Files();
    private int numberConnect;
    public MonoThreadClientHandler(Socket client,int numberConnect) throws IOException {
        MonoThreadClientHandler.clientDialog = client;
        in = new DataInputStream(clientDialog.getInputStream());
        number = new DataInputStream(clientDialog.getInputStream());
        out = new DataOutputStream(clientDialog.getOutputStream());
        this.numberConnect=numberConnect;
    }

    @Override
    public void run() {
        try {
            while (!clientDialog.isClosed()) {
                if (menu() == 0)
                    break;
            }
            System.out.print("Connection disconnected.\n");
            System.out.println("подключений за все время работы сервера: "+numberConnect);
            clientDialog.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int menu() throws Exception {
        while (true) {
            /*1.проверить пароль логин админа*/
            /*2.проверить пароль логин пользователей*/
            /*3. получить новый пароль и логин администратора
            /*4.записать зарегестрированного пользователя*/
            /*5.послать имя фамилия логин пароль для просмотра админу*/
            /*6.записать список пользователей после удаления админом*/
            /*7.послать кол-во пользователей */
            /*8.проверить уникальность логина*/
            /*9.получить новую заявку*/
            /*10. закрыть сокет*/
            /*11.отправить все заявки*/
            /*12. получить новый список заявок*/
            switch (number.read()) {
                case 1:
                    checkAdminLoginPasswords();
                    break;
                case 2:
                    checkUserLoginPasswords();
                    break;
                case 3:
                    getNewAdminLoginPassword();
                    break;
                case 4:
                    getNewUserData();
                    break;
                case 5:
                    sendUserData();
                    break;
                case 6:
                    getNewUsersList();
                    break;
                case 7:
                    sendUsersNumber();
                    break;
                case 8:
                    checkRepeatLogin();
                    break;
                case 9:
                    getNewApplication();
                    break;
                case 10:
                    return 0;
                case 11:
                    sendApplicationData();
                    break;
                case 12:
                    getNewApplicationList();
                    break;
                case 13:
                    getMethodInformation();
                    break;
                case 14:
                    sendResult();
                    break;
                case 15:
                    sendSizeMethodInformation();
                    break;
                case 16:
                    files.clearFile(MethodInformation);
                    break;
                case 17:
                    saveAnswer();
                    break;
                case 18:
                    getLogin();
                    break;
            }
        }
    }

    private void checkAdminLoginPasswords() throws IOException {
        Admin admin;
        admin = files.readAdminAccount();
        if (files.fileLength == 0){
            admin.getAccount().setLogin("");
        admin.getAccount().setPassword("");
        }
            out.write(admin.getAccount().comparison(in.readUTF(), in.readUTF(), 1));

    }

    private void checkUserLoginPasswords() throws Exception {
        List<User> users;
        int flag = 0;
        files.readUserNumber();
        users = files.readUserAccount();
        String login = in.readUTF();
        String password = in.readUTF();
        if (users.size() != 0) {
            for (User user : users) {
                if (user.getAccount().comparison(login, password, 2) == 2) {
                    flag = 1;
                    out.write(2);
                    break;
                }
            }
            if (flag == 0) out.write(0);
        } else out.write(0);
    }

    private void getNewAdminLoginPassword() throws IOException {
        Admin admin = new Admin();
        admin.getAccount().setLogin(in.readUTF());
        admin.getAccount().setPassword(in.readUTF());
        files.writeAdminAccount(admin);
    }

    private void getNewUserData() throws Exception {
        User user = new User();
        List<User> users = new ArrayList();
        user.setName(in.readUTF());
        user.setSurname(in.readUTF());
        user.getAccount().setLogin(in.readUTF());
        user.getAccount().setPassword(in.readUTF());
        users.add(user);
        files.writeUserAccount(users);
    }

    private void sendUserData() throws Exception {
        List<User> users = new ArrayList();
        files.readUserNumber();
        out.write(files.getUserNumber());
        if (files.getUserNumber() != 0)
            users = files.readUserAccount();
        if (files.getUserNumber() != 0) {
            for (int i = 0; i < files.getUserNumber(); i++) {
                out.writeUTF(users.get(i).getName());
                out.writeUTF(users.get(i).getSurname());
                out.writeUTF(users.get(i).getAccount().getLogin());
                out.writeUTF(users.get(i).getAccount().getLogin());
            }
        }
    }

    private void getNewUsersList() throws Exception {

        List<User> users = new ArrayList();
        int userNumber = in.read();
        for (int i = 0; i < userNumber; i++) {
            User user = new User();
            user.setName(in.readUTF());
            user.setSurname(in.readUTF());
            user.getAccount().setLogin(in.readUTF());
            user.getAccount().setPassword(in.readUTF());
            users.add(user);
        }
        files.clearFile(UserAccount);
        files.clearFile(UserNumber);
        files.writeUserAccount(users);
    }

    private void sendUsersNumber() throws Exception {
        files.readUserNumber();
        out.write(files.getUserNumber());
    }

    private int checkRepeatLogin() throws Exception {
        List<User> users;
        Admin admin;
        files.readUserNumber();
        admin = files.readAdminAccount();
        users = files.readUserAccount();
        String login = in.readUTF();
        if (files.fileLength != 0) {
            if (admin.getAccount().getLogin().equals(login)) {
                out.write(0);
                return 0;
            }
        }
        if (users.size() != 0) {
            for (User user : users) {
                if (user.getAccount().getLogin().equals(login)) {
                    out.write(0);
                    return 0;
                }
            }
            out.write(1);
            return 0;
        }
        out.write(1);
        return 0;
    }

    private void getNewApplication() throws Exception {
        List<Application> applications = new ArrayList();
        User user = new User();
        user.setName(in.readUTF());
        user.setSurname(in.readUTF());
        Car car = new Car();
        car.setYearRelease(in.readUTF());
        car.setCarModel(in.readUTF());
        car.setCarBrand(in.readUTF());
        car.setVinNumber(in.readUTF());
        car.setRegistrationNumber(in.readUTF());
        user.setCar(car);
        Application application = new Application();
        application.setUser(user);
        applications.add(application);
        files.writeApplication(applications);

    }

    private void sendApplicationData() throws Exception {
        List<Application> applications = files.readApplications();
        out.write(applications.size());
        for (Application application : applications) {
            out.writeUTF(application.getUser().getName());
            out.writeUTF(application.getUser().getSurname());
            out.writeUTF(application.getUser().getAccount().getLogin());
            out.writeUTF(application.getUser().getCar().getYearRelease());
            out.writeUTF(application.getUser().getCar().getCarModel());
            out.writeUTF(application.getUser().getCar().getCarBrand());
            out.writeUTF(application.getUser().getCar().getVinNumber());
            out.writeUTF(application.getUser().getCar().getRegistrationNumber());
        }
    }

    private void getNewApplicationList() throws Exception {
        List<Application> applications = new ArrayList();
        int applicationNumber = in.read();
        for (int i = 0; i < applicationNumber; i++) {
            User user = new User();
            user.setName(in.readUTF());
            user.setSurname(in.readUTF());
            Car car = new Car();
            car.setYearRelease(in.readUTF());
            car.setCarModel(in.readUTF());
            car.setCarBrand(in.readUTF());
            car.setVinNumber(in.readUTF());
            car.setRegistrationNumber(in.readUTF());
            user.setCar(car);
            Application application = new Application();
            application.setUser(user);
            applications.add(application);
        }
        files.clearFile(ApplicationInformation);
        files.writeApplication(applications);
    }

    private void getMethodInformation() throws IOException {
        files.writeMethodInformation(in.readUTF());
    }
    private void sendResult() throws IOException {
        MethodExpertEvaluation methodInformation=files.readMethodInformation();
        String services=files.readService();
        methodInformation.findRj();
        methodInformation.findDRj();
        methodInformation.findCk();
        methodInformation.findDCk();
        methodInformation.findBest(services);
        out.writeUTF(methodInformation.toString(methodInformation.getRj()));
        out.writeUTF(methodInformation.toString(methodInformation.getdRj()));
        out.writeUTF(methodInformation.toString(methodInformation.getdCk()));
        out.writeUTF(services);
        out.writeUTF(files.readPrice());
        files.report(services,methodInformation.getBest());
        out.writeUTF(methodInformation.toString(methodInformation.getCk()));
    }
    private void sendSizeMethodInformation() throws IOException {
        out.write(files.getMethodInformationSize());
}
    private void saveAnswer() throws IOException {
        Answer answer=new Answer();
        Account account=new Account();
        account.setLogin(files.findApplicationLogin());
        answer.setAccount(account);
        answer.setService(in.readUTF());
        answer.setPrice(in.readUTF());
files.writeAnswerUser(answer);
}
        private void getLogin() throws IOException {
            List<Answer>answers=new ArrayList();
            answers=files.readAnswerUser();
            if(answers.size()==0) {
            in.readUTF();
            out.writeUTF("0");
            }
            else{
             String login=in.readUTF();
             for(Answer answer:answers){
                 if(!answer.equals(login)){
                     answers.remove(answer);
                     if(answers.size()==0)break;
                 }
             }
             out.writeUTF(Integer.toString(answers.size()));
             for(Answer answer:answers){
                 out.writeUTF(answer.getTime());
                 out.writeUTF(answer.getService());
                 out.writeUTF(answer.getPrice());
             }
            }
        }
}