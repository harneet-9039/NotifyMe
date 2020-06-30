package app.com.models;

import java.util.ArrayList;
import java.util.Comparator;

public class ViewRequestModel {
    public static Comparator<ViewRequestModel> priorityComparator = new Comparator<ViewRequestModel>() {

        public int compare(ViewRequestModel s1, ViewRequestModel s2) {
            String priorityOne = s1.getDate().toUpperCase();
            String prioritySecond = s2.getDate().toUpperCase();

            //ascending order
            //return priorityOne.compareTo(prioritySecond);

            //descending order
            return prioritySecond.compareTo(priorityOne);
        }
    };

    private String RequestId;
    private String Facultyid;
    private String FacultyName;
  //  private String StudentId;
   // private String StudentName;
  //  private String Status;
    private String date;
    private String validity;
    private ArrayList<String> names;
    private ArrayList<String>registrationID;
    private ArrayList<String> status;


    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public String getFacultyid() {
        return Facultyid;
    }

    public void setFacultyid(String facultyid) {
        Facultyid = facultyid;
    }

    public String getFacultyName() {
        return FacultyName;
    }

    public void setFacultyName(String facultyName) {
        FacultyName = facultyName;
    }

   /* public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
*/

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getValidity() {
        return validity;
    }

    public static Comparator<ViewRequestModel> getPriorityComparator() {
        return priorityComparator;
    }

    public static void setPriorityComparator(Comparator<ViewRequestModel> priorityComparator) {
        ViewRequestModel.priorityComparator = priorityComparator;
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

    public void setValidity(String validity) {
        this.validity = validity;
    }
}
