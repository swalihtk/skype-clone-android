package io.swalitk.github.skypeclone;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class All_Users {

    final String REQUEST_URL="https://netflix-clone-23-default-rtdb.firebaseio.com/Users.json";
    Context context;
    All_Users(Context context){
        this.context=context;
    }

    public interface Response_error_listener{
        public void onError(String err);
        //public void onSuccuss(ArrayList<All_users_list> object);
        public void onSuccuss(ArrayList<All_users_list> users_lists);
    }

    public void getTotalUsers(Response_error_listener response_error_listener){
      ArrayList<All_users_list> arrayList=new ArrayList<>();
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, REQUEST_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONArray array=response.names();
                    for(int i=0; i<array.length(); i++){
                        JSONObject object=response.getJSONObject(array.get(i).toString());
                        if(object.getString("uid").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            continue;
                        }
                        All_users_list users_list=new All_users_list();
                        users_list.setUsername(object.getString("username"));
                        users_list.setUid(object.getString("uid"));
                        users_list.setImageUrl(object.getString("imageUrl"));
                        arrayList.add(users_list);
                    }
                    response_error_listener.onSuccuss(arrayList);
                }catch (Exception e){
                    e.printStackTrace();
                    response_error_listener.onError(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                response_error_listener.onError(error.toString());
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);
    }
}
