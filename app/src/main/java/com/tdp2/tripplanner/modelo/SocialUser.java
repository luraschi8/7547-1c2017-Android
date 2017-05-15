package com.tdp2.tripplanner.modelo;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Casa on 13/5/2017.
 */

public class SocialUser {



    private String _id;
    private String token;
    public Profile profile;


    public SocialUser() {
    }

    public SocialUser(SocialUser other) {
        this._id = other._id;
        this.token = other.token;
        if (other.profile != null) {
            this.profile = new Profile(other.profile);
        }
    }
    public String getName() {
        return this.profile.name;
    }

    public void setName(String name) {
        this.profile.name = name;
    }

    public String get_id ()
    {
        return _id;
    }

    public void set_id (final String _id)
    {
        this._id = _id;
    }

    public String getEmail ()
    {
        return this.profile.email;
    }

    public void setEmail (final String email)
    {
        this.profile.email = email;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(final String token)
    {
        this.token = token;
    }

    public JSONObject GetJsonObject() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", this.profile.email);
        params.put("Name", this.profile.name);
        params.put("token", token);
        params.put("id", _id);

        return new JSONObject();
    }

    public void setPhotoURL(String photoURL) {
        this.profile.photoURL = photoURL;
    }

    public String getPhotoURL() {
        return this.profile.photoURL ;
    }

    @Override
    public String toString()
    {
        return "SocialUser [name = "+this.profile.name+", _id = "+_id+", email = "+this.profile.email+", token = "+ token +", profile = "+profile+"]";
    }

    public static class Profile implements Serializable {
        public String name;
        public String email;
        public String pageLink;
        public String photoURL;

        public Profile() {
        }

        public Profile(Profile other) {
            this.name = other.name;
            this.email = other.email;
            this.pageLink = other.pageLink;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Profile{");
            sb.append("name='").append(name).append('\'');
            sb.append(", email='").append(email).append('\'');
            sb.append(", pageLink='").append(pageLink).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
