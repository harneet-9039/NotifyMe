package app.com.notifyme;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import app.com.common.GlobalMethods;
import app.com.common.Singleton;
import app.com.models.Course;
import app.com.models.Department;

public class createNotice extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private Spinner Course_Spinner, Department_Spinner,Scope_Spinner,Year_Spinner;
    private TextView Attachment,Image;

    private ArrayList<app.com.models.Department> Department;
    private ArrayList<app.com.models.Course> Course;

    private static int DepartmentID, CourseID;
    private RecyclerView recyclerView;
    static final int REQUEST_IMAGE_CAPTURE=1;
    private static final int PICKFILE_RESULT_CODE = 8778;
    private View v;
    private File bannerDirectory;
    private File attachmentDirectory;
    private Uri uri;
    private int flag = 0;
    private ImageView i;
    private int set_scope=0;
    private int attachment_count=0;
    private int image_count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);

        Department_Spinner=findViewById(R.id.spinner1);
        Course_Spinner=findViewById(R.id.spinner2);
        Scope_Spinner=findViewById(R.id.spinner);
        Year_Spinner=findViewById(R.id.spinner3);
        progressDialog = new ProgressDialog(this);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        bannerDirectory = cw.getDir("attachment", Context.MODE_PRIVATE);
        attachmentDirectory= cw.getDir("banner",Context.MODE_PRIVATE);
        UpdateDocumentVariable();
       Update_Attachments();
       Update_Images();



        Department=new ArrayList<>();
        Course=new ArrayList<>();



        Scope_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String selected=Scope_Spinner.getSelectedItem().toString();
                if(selected.equals("Public"))
                {
                    Department_Spinner.setEnabled(false);
                    Course_Spinner.setEnabled(false);
                    Year_Spinner.setEnabled(false);
                }

                else if(selected.equals("Department Level"))
                {
                    if(set_scope==0) {
                        FillDepartment();
                        set_scope=1;
                    }

                    Department_Spinner.setEnabled(true);
                    Course_Spinner.setEnabled(false);
                    Year_Spinner.setEnabled(false);
                }
                else if(selected.equals("Course Level"))
                {

                    Initialize();
                    Department_Spinner.setEnabled(true);
                    Course_Spinner.setEnabled(true);
                    Year_Spinner.setEnabled(false);
                }

                else if(selected.equals("Year Level"))
                {

                  Initialize();
                    Department_Spinner.setEnabled(true);
                    Course_Spinner.setEnabled(true);
                    Year_Spinner.setEnabled(true);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        v = findViewById(R.id.root);


        ImageView imageView2= findViewById(R.id.imageView2);
        TextView textView7= findViewById(R.id.textView7);
        i = findViewById(R.id.imageView3);


        if(!hasCamera()) {
            imageView2.setEnabled(false);
            textView7.setEnabled(false);
        }

    }

    private void UpdateDocumentVariable() {
        File childfile[] = attachmentDirectory.listFiles();
        File childFile[] = bannerDirectory.listFiles();
        for (File file2 : childfile) {
            attachment_count++;
        }
        for(File file: childFile)
        {
            image_count++;
        }

    }

    private void Initialize() {

        if(set_scope==0) {
            FillDepartment();
            set_scope=1;
        }
            Department_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i > 0) {
                        final Department department = (Department) Department_Spinner.getItemAtPosition(i);
                        Log.d("HAR", "onItemSelected: country: " + department.GetDepartmentID());
                        DepartmentID = department.GetDepartmentID();
                        Course.clear();
                        FillCourses(department.GetDepartmentID());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            Course_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i > 0) {
                        final Course course = (Course) Course_Spinner.getItemAtPosition(i);
                        CourseID = course.GetCourseID();
                        Log.d("HAR", "onItemSelected: course: " + course.GetCourseID());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }


    private void FillCourses(final int DepartmentID)
    {
        Course.add(new Course(0, "Course"));
        final String URL = GlobalMethods.getURL()+"course";
        progressDialog.show();

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //JSONArray response = null;
                Log.d("HAR",response.toString());
                try {
                    JSONObject j = new JSONObject(response);
                    JSONArray data = j.getJSONArray("data");
                    for(int i=0;i<data.length();i++)
                    {
                        JSONObject jsonObject1 = data.getJSONObject(i);
                        Course.add(new Course(jsonObject1.getInt("Course_id"),
                                jsonObject1.getString("Course_branch")));
                    }
                    Course_Spinner.setAdapter(new ArrayAdapter<Course>(createNotice.this,R.layout.simple_spinner_dropdown_item,Course));
                    progressDialog.dismiss();
                }
                catch (JSONException e){

                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("HAR",error.toString());
                progressDialog.dismiss();


            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                Log.d("HAR",String.valueOf(DepartmentID));
                parameters.put("dept_id", String.valueOf(DepartmentID));
                return parameters;
            }
        };
        Singleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void FillDepartment() {
        Department.add(new Department(0, "Department"));
        final String URL = GlobalMethods.getURL() + "department";
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //JSONArray response = null;
                        Log.d("HAR", response.toString());
                        try {
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject jsonObject1 = data.getJSONObject(i);
                                Department.add(new Department(jsonObject1.getInt("Dept_id"),
                                        jsonObject1.getString("Dept_name")));
                            }
                            Department_Spinner.setAdapter(new ArrayAdapter<Department>(createNotice.this, R.layout.simple_spinner_dropdown_item, Department));
                            progressDialog.dismiss();
                        } catch (JSONException e) {

                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HAR", error.toString());
                        progressDialog.dismiss();


                    }
                });

        Singleton.getInstance(this).addToJsonRequestQueue(jsonObjectRequest);
    }


    public void attachment_file_upload(View view){
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        flag=1;
        startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
    }

    public void BannerImageUpload(View view){

        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("image/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose an image");
        flag=2;
        startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);

    }

    private String saveToInternalStorage(Bitmap bitmapImage, String imageName, String fileName, String filePath) throws IOException {
        if(bitmapImage == null && imageName == null){

            File destination = new File(attachmentDirectory, fileName);
            FileOutputStream fn=null;
                try {
                    fn = new FileOutputStream(destination);
                    byte[] mybytes = filePath.getBytes();
                    fn.write(mybytes);
                    File file = new File(attachmentDirectory, fileName);
                    if (file.exists()) {
                        Snackbar.make(v, "Document Saved Successfully",
                                Snackbar.LENGTH_LONG)
                                .show();
                        attachment_count++;
                        Update_Attachments();
                        flag=0;
                    } else {
                        Snackbar.make(v, "File uploaded but not saved",
                                Snackbar.LENGTH_LONG)
                                .show();
                        flag=0;
                    }
                } catch (Exception e) {
                    flag=0;
                    e.printStackTrace();
                }
        }
        else {

            File destination;
            if(flag==1) {
                    destination = new File(attachmentDirectory, imageName);
            }
            else {
                destination = new File(bannerDirectory, imageName);

            }



            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(destination);
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                final File file1 = new File(String.valueOf(destination));
                if(file1.exists()) {
                    Snackbar.make(v, "Image Saved Successfully",
                            Snackbar.LENGTH_LONG)
                            .show();
                    if(flag==1) {
                        attachment_count++;
                        Update_Attachments();
                    }
                    else
                    {
                        image_count++;
                        Update_Images();
                    }
                    flag=0;
                }else{
                    Snackbar.make(v, "Image Save not worked",
                            Snackbar.LENGTH_LONG)
                            .show();
                    flag=0;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            } finally{
                try {
                    flag=0;
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return attachmentDirectory.getAbsolutePath();
    }

    private void loadImageFromStorage(String path, String fileName)
    {

        try {
            File f=new File(path,fileName );
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=findViewById(R.id.imageView3);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    public boolean hasCamera()
    {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public void  att_camera(View view)
    {
        if(view.getId()==R.id.textView7 || view.getId()==R.id.imageView2){
        flag = 1;}
        else{
            flag=2;
        }
        Intent i2=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i2,REQUEST_IMAGE_CAPTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK) {

            String path = new File(data.getData().getPath()).getAbsolutePath();
            if (path != null) {
                uri = data.getData();
                String filename;
                String ext = path.substring(path.lastIndexOf(".") + 1);

                Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                if (cursor == null) filename = uri.getPath();
                else {
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    filename = cursor.getString(idx);
                    cursor.close();
                }
                try {
                    String finalName = "ATT_" + filename;
                    if(!ext.equals("pdf")&&!ext.equals("docx")&&!ext.equals("doc")&&!ext.equals("pptx")&&!ext.equals("ppt")) {

                            Bitmap bitmap =  MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            saveToInternalStorage(bitmap, finalName, "", path);
                        }

                    else {
                        saveToInternalStorage(null, null, finalName, path);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Log.d("HAR", "ki9ol9kk");
                Bundle extras = data.getExtras();
                final Bitmap photo = (Bitmap) extras.get("data");
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                View mView = getLayoutInflater().inflate(R.layout.my_custom_dialog, null);

                final EditText dialog_editText = (EditText) mView.findViewById(R.id.dialog_editText);
                Button dialog_cancel = (Button) mView.findViewById(R.id.dialog_cancel);
                Button dialog_save = (Button) mView.findViewById(R.id.dialog_save);

                alert.setView(mView);

                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);

                dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                dialog_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Random r = new Random();
                        int n = (100000 + r.nextInt(900000));
                        final String fileName = dialog_editText.getText().toString() + "-" + String.valueOf(n) + ".png";
                        try { saveToInternalStorage(photo, fileName, "","");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();


            }
        }

    private File copyToTempFile(Uri uri, File tempFile) throws IOException {
        // Obtain an input stream from the uri
        InputStream inputStream = getContentResolver().openInputStream(uri);

        if (inputStream == null) {
            throw new IOException("Unable to obtain input stream from URI");
        }

        // Copy the stream to the temp file
       // FileUtils.copyInputStreamToFile(inputStream, tempFile);

        return tempFile;
    }

    public void arrow_click(View view) {
        Intent i1=new Intent(this,NoticeDashboard.class);
        startActivity(i1);
    }

    public void counter_attachment(View view){

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(createNotice.this);
        builderSingle.setIcon(R.drawable.add_file);
        builderSingle.setTitle("List of Attachments");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(createNotice.this,R.layout.closebutton_layout);
        File childfile[] = attachmentDirectory.listFiles();
        for (File file2 : childfile) {
            arrayAdapter.add(file2.getName());
            Log.d("HAR", file2.getName().toString());
        }


        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(createNotice.this);
                builderInner.setMessage("File name: "+strName + " will be deleted.");
                builderInner.setTitle("Are you sure to delete this file?");
                builderInner.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        String fileName = strName;
                        File path = new File(attachmentDirectory, fileName);
                        if(path.delete()){
                            attachment_count--;
                            Update_Attachments();
                            Snackbar.make(v, "File deleted successfully",
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                        else{
                            Snackbar.make(v, "There was an error deleting file",
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
                builderInner.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }

    public void counter_image(View view){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(createNotice.this);
        builderSingle.setIcon(R.drawable.add_file);
        builderSingle.setTitle("List of Banner Image");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(createNotice.this,R.layout.closebutton_layout);
        File childfile[] = bannerDirectory.listFiles();
        for (File file2 : childfile) {
            arrayAdapter.add(file2.getName());
            Log.d("HAR", file2.getName().toString());
        }


        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(createNotice.this);
                builderInner.setMessage("File name: "+strName + " will be deleted.");
                builderInner.setTitle("Are you sure to delete this file?");
                builderInner.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        String fileName = strName;
                        File path = new File(bannerDirectory, fileName);
                        if(path.delete()){
                            image_count--;
                            Update_Images();
                            Snackbar.make(v, "Image deleted successfully",
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                        else{
                            Snackbar.make(v, "There was an error deleting file",
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
                builderInner.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }

    private void Update_Attachments(){
        Attachment=findViewById(R.id.counter_attachment);

        if(attachment_count==0) {
            Attachment.setVisibility(View.INVISIBLE);

        }
        else if(attachment_count==1){
            Attachment.setVisibility(View.VISIBLE);
            Attachment.setText(String.valueOf(attachment_count));
        }
        else{
            Attachment.setText(String.valueOf(attachment_count));
        }
    }

    private void Update_Images(){
        Image=findViewById(R.id.counter_image);
        if(image_count==0) {
            Image.setVisibility(View.INVISIBLE);

        }
        else if(image_count==1){
            Image.setVisibility(View.VISIBLE);
            Image.setText(String.valueOf(image_count));
        }
        else{
            Image.setText(String.valueOf(image_count));
        }
    }

    public void img_camera(View view) {
    }
}