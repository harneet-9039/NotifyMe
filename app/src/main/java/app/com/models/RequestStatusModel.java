package app.com.models;

import java.util.ArrayList;
import java.util.Comparator;

public class RequestStatusModel {
    public static Comparator<RequestStatusModel> priorityComparator = new Comparator<RequestStatusModel>() {

        public int compare(RequestStatusModel s1, RequestStatusModel s2) {
            String priorityOne = s1.getDate().toUpperCase();
            String prioritySecond = s2.getDate().toUpperCase();

            //ascending order
            //return priorityOne.compareTo(prioritySecond);

            //descending order
            return prioritySecond.compareTo(priorityOne);
        }};
    private String requestID;
    private String date;
    private String validity;
    private ArrayList<String> names;
    private ArrayList<String>registrationID;
    private ArrayList<String> status;

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }

    public ArrayList<String> getRegistrationID() {
        return registrationID;
    }

    public void setRegistrationID(ArrayList<String> registrationID) {
        this.registrationID = registrationID;
    }

    public ArrayList<String> getStatus() {
        return status;
    }

    public void setStatus(ArrayList<String> status) {
        this.status = status;
    }
}
