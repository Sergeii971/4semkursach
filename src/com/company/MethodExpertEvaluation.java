package com.company;

import java.util.Scanner;

public class MethodExpertEvaluation {
    MethodExpertEvaluation(){rj=new double[5];dRj=new double[5];}
    private int [][] expertMatrix;
    private int [][] serviceMatrix;
    private double [] rj;
    private double [] ck;
    private double [] dRj;
    private double [] dCk;
    private String best;
    private int serviceNumber;
    public void setBest(String best) { this.best = best; }
    public String getBest() { return best; }

    public void setExpertMatrix(int [][] expertMatrix){this.expertMatrix=expertMatrix;}
    public int[][] getExpertMatrix() { return expertMatrix; }
    public void setServiceMatrix(int[][] serviceMatrix) { this.serviceMatrix = serviceMatrix; }
    public int[][] getServiceMatrix() { return serviceMatrix; }
    public void setServiceNumber(int serviceNumber) { this.serviceNumber = serviceNumber; }
    public int getServiceNumber() { return serviceNumber; }
    public String toString(double[]matrix){
        String mass="";
        for(double i:matrix){
            mass+=Double.toString(i)+"\n";
        }
        return mass;
    }
    public double[] getdCk() { return dCk; }
    public double[] getdRj() { return dRj; }
    public double[] getRj() { return rj; }
    public double[] getCk() { return ck; }

    public void findRj(){
        double sumAllMatrix=0;
        for(int i=0;i<5;i++)
            for(int j=0;j<5;j++)
                sumAllMatrix+=expertMatrix[i][j];

        for(int j=0;j<5;j++){
            for(int i=0;i<5;i++){
                rj[j]+=expertMatrix[i][j];
            }
            rj[j]/= sumAllMatrix;
        }
    }
    public void findCk() {
        ck=new double[serviceNumber];
        double sumAllMatrix=0;
        for(int i=0;i<serviceNumber;i++)
            for(int j=0;j<5;j++)
                sumAllMatrix+=(double) serviceMatrix[i][j]*rj[j];
        for(int k=0;k<serviceNumber;k++){
            for(int i=0;i<5;i++){
                ck[k]+= serviceMatrix[k][i]*rj[i];
            }
            ck[k]/=sumAllMatrix;
        }
    }
    public void findDRj(){
        double sumMurk;
        for(int i=0;i<5;i++){
            sumMurk=0;
            for(int j=0;j<5;j++) {
                if (i != j)
                    sumMurk += expertMatrix[i][j];
            }
            sumMurk/=4;
            for(int j=0;j<5;j++){
                if(i!=j){
                    dRj[i]+=Math.pow(expertMatrix[i][j]-sumMurk,2);
                }
            }
            dRj[i]/=3;
        }
    }
    public void findDCk(){
        dCk=new double[5];
        double sumMurk;
        for(int i=0;i<5;i++){
            sumMurk=0;
            for(int j=0;j<serviceNumber;j++)
                    sumMurk+=serviceMatrix[j][i];
            sumMurk/=4;
            for(int j=0;j<serviceNumber;j++){
                    dCk[i]+=Math.pow(serviceMatrix[j][i]-sumMurk,2);
            }
            dCk[i]/=3;
        }
    }
    public void findBest(String services){
        double max=0;
        String service="";
        Scanner scanner=new Scanner(services);
        for(double i:ck){
            service=scanner.nextLine();
            if(i>max){
                best=service;
                max=i;
            }
        }
    }
}
