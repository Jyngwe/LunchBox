package com.example.controller;

import com.example.domain.LunchBox;
import com.example.domain.Person;
import com.example.domain.User;
import com.example.repository.Repository;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.PendingResult;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.naming.Binding;
import javax.servlet.http.HttpSession;
import javax.swing.*;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
public class LogicController {
    ArrayList<User> users;
    ArrayList<Person> persons;
    ArrayList<LunchBox> lunchBoxes;
    String lunchBoxesJson;
    boolean showNewUser = false;
    boolean showLogin = false;



    @Autowired
    Repository repository;

    @PostConstruct
    public void RefreshUsers() {
        users = (ArrayList<User>) repository.getUsers();
    }
    @PostConstruct
    public void RefreshPersons() {
        persons = (ArrayList<Person>) repository.getPersons();
    }
    @PostConstruct
    public void RefresshLunchBoxes() {
        lunchBoxes = (ArrayList<LunchBox>) repository.getLunchBoxes();
        lunchBoxesJson = objectToJSON(lunchBoxes);

    }


    @PostMapping("/login")
    public ModelAndView getUserLogin(@RequestParam String userName, HttpSession session, @RequestParam String password) throws Exception {
        for (User index : users) {
            if((userName.equals(index.getUserName()) && (password.equals(index.getPassword())))) {
                User user = index;
                session.setAttribute("user", index);
                session.setAttribute("person", persons.get(index.getUserID()) );
                LunchBox lunchbox = new LunchBox(lunchBoxes.size()+1, "PANNKAKA", "", null, null, false, false, false, false, false, false, false, false, null, 0);
                String location = "Borgarfjordsgatan";

                return new ModelAndView("userSession")
                        .addObject("userSession", session)
                        .addObject("user", index)
                        .addObject("person", persons.get(index.getUserID()) )
                        .addObject("lunchBoxes", lunchBoxesJson)
                        .addObject("lunchbox", lunchbox)
                        .addObject("location", location);

            }

        }
        showLogin = true;
        User user = new User(userName, password, "");
        Person person = new Person("", "", "");
        String incorr = "Username or password is incorrect.";
        return new ModelAndView("index")
                .addObject("showLogin", showLogin)
                .addObject("incorrLogin", incorr)
                .addObject("lunchBoxes", lunchBoxesJson)
                .addObject("user", user)
                .addObject("person", person);

    }

    @GetMapping("/")
    public ModelAndView form() {


        User user = new User("", "", "");
        Person person = new Person("", "", "");
        ModelAndView mv = new ModelAndView("index");
        mv.addObject("user",user);
        mv.addObject("person",person);
        mv.addObject("lunchBoxes", lunchBoxesJson);


        return mv;
}

    @GetMapping("/userSession")
    public ModelAndView userSession() {
        return null;
    }

    @PostMapping("/user")
    public ModelAndView newUser(@Valid User user, BindingResult bru, @Valid Person person, BindingResult brp, RedirectAttributes attr) throws Exception {

        if (bru.hasErrors() || brp.hasErrors() ||   userNameDuplicate(user)) {

            showNewUser = true;
            String error = bru.getFieldError().getField() + " " + bru.getFieldError().getDefaultMessage();

            return new ModelAndView("index")
                    .addObject("showNewUser", showNewUser)
                    .addObject("error", error)
                    .addObject("lunchBoxes", lunchBoxesJson);

        }

        int key = Integer.parseInt(repository.addUser(user, person));
        users.add(new User(key, user.getUserName(), user.getPassword(), user.getMail()));
        persons.add(new Person(key, person.getFirstName(), person.getLastName(), person.getPhoneNumber()));
        return new ModelAndView("Adam");
    }


    @PostMapping("/lunchbox")
    public ModelAndView newLunchBox(LunchBox lunchbox, String location, HttpSession session) throws SQLException {

        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyBTZQRmcgBi0Fw0rNCsKoUBZohWk7UW0dw&");
        GeocodingApiRequest req = GeocodingApi.newRequest(context).address(location);

        Object person = session.getAttribute("person");


        GeocodingResult[] results = req.awaitIgnoreError();
        for(GeocodingResult result : results) {
            BigDecimal lat = new BigDecimal(result.geometry.location.lat);
            lat = lat.setScale(6, RoundingMode.FLOOR);
            lunchbox.setLatitud(lat);

            BigDecimal lng = new BigDecimal(result.geometry.location.lng);
            lng = lng.setScale(6, RoundingMode.FLOOR);
            lunchbox.setLongitud(lng);
        }

        repository.addLunchBox(lunchbox);
        lunchBoxes.add(lunchbox);

        return new ModelAndView("userSession")
                .addObject("lunchBoxes", lunchBoxesJson)
                .addObject("lunchbox", lunchbox)
                .addObject("location", location);
    }

    public boolean userNameDuplicate(User user) {
        boolean duplicate = false;

        for(User index : users) {
            if(index.getUserName().equals(user.getUserName())){
                duplicate = true;
                return duplicate;
            }
        }return duplicate;
    }


    public String objectToJSON(ArrayList<LunchBox> array) {
        ObjectMapper mapper  = new ObjectMapper();
        String jsonInString = "[";
            for(int i = 0; i<array.size(); i++) {
                try {
                    jsonInString += mapper.writeValueAsString(array.get(i));
                    if(i<array.size()-1) {
                        jsonInString += ",";
                    }

                }
                catch(JsonGenerationException e) {
                    e.printStackTrace();
                }
                catch(JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            jsonInString += "]";
        return jsonInString;
    }


}
