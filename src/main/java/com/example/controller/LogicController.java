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
import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;


@Controller
public class LogicController {
    ArrayList<User> users;
    ArrayList<Person> persons;
    ArrayList<LunchBox> lunchBoxes;
    String lunchBoxesJson;
    String personJson;
    boolean showNewUser = false;
    boolean showLogin = false;
    boolean newFood = false;
    boolean updatedSettings = false;

    @Autowired
    Repository repository;


    @PostConstruct
    public void RefreshUsers() {
        users = (ArrayList<User>) repository.getUsers();
    }
    @PostConstruct
    public void RefreshPersons() {
        persons = (ArrayList<Person>) repository.getPersons();
        personJson = personToJSON(persons);
    }
    @PostConstruct
    public void RefresshLunchBoxes() {
        lunchBoxes = (ArrayList<LunchBox>) repository.getLunchBoxes();
        lunchBoxesJson = objectToJSON(lunchBoxes);

    }

// Om någon redan är inloggad skickas direkt till "userSession", annars visas startsidan
    @GetMapping("/")
    public ModelAndView form(HttpSession session) {
        LunchBox lunchbox = new LunchBox(lunchBoxes.size()+1, "", "", null, null, false, false, false, false, false, false, false, false, null, 0, 0, 0);

        if (session.getAttribute("user") != null) {
            return new ModelAndView("userSession")
                    .addObject("userSession", session)
                    .addObject("user", session.getAttribute("user"))
                    .addObject("person", returnCorrectPerson(((User)session.getAttribute("user")).getUserID()) )
                    .addObject("lunchBoxes", lunchBoxesJson)
                    .addObject("lunchbox", lunchbox)
                    .addObject("persons", personJson);
        }
        User user = new User("", "", "");
        Person person = new Person("", "", "");

        return new ModelAndView("index")
        .addObject("user",user)
        .addObject("person",person)
        .addObject("lunchBoxes", lunchBoxesJson)
        .addObject("persons", personJson);
    }
//Login-funktion. Om användaren loggar in med befintligt username och password skickas till "userSession", annars visas loginrutan
    //igen och felmeddelande visas
    @PostMapping("/login")

    public ModelAndView getUserLogin(@RequestParam String userName, HttpSession session, @RequestParam String password, LunchBox lunchBox) throws Exception {

        for (User index : users) {
            if((userName.equals(index.getUserName()) && (password.equals(index.getPassword())))) {
                session.setAttribute("user", index);
                session.setAttribute("person", returnCorrectPerson(index.getUserID()) );
                String location = "";

                return new ModelAndView("userSession")
                        .addObject("userSession", session)
                        .addObject("user", index)
                        .addObject("person", returnCorrectPerson(index.getUserID()) )
                        .addObject("lunchBoxes", lunchBoxesJson)
                        .addObject("persons", personJson)
                        .addObject("lunchbox", lunchBox)
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


//Vid skapande av ny user visas felmeddelande om validation fails eller om username redan finns. Annars skapas ny user och
    //användaren redirectas till startsidan + loggas in automatiskt.
    @PostMapping("/user")

    public ModelAndView newUser(@Valid User user, BindingResult bru, @Valid Person person, BindingResult brp, RedirectAttributes attr, HttpSession session) throws Exception {
        personJson = personToJSON(persons);
        if (bru.hasErrors() || brp.hasErrors() || userNameDuplicate(user)) {
            showNewUser = true;
            String error = "";
            if (brp.hasErrors()){
                error = brp.getFieldError().getDefaultMessage();
            }
            else if(bru.hasErrors()){
                error = bru.getFieldError().getDefaultMessage();
            }if(userNameDuplicate(user)){
                error = "Användarnament är upptaget, vänligen välj ett nytt";
            }return new ModelAndView("index")
                    .addObject("showNewUser", showNewUser)
                    .addObject("error", error)
                    .addObject("lunchBoxes", lunchBoxesJson)
                    .addObject("persons", personJson)
            ;
        }

        LunchBox lunchbox = new LunchBox(lunchBoxes.size()+1, "", "", null, null, false, false, false, false, false, false, false, false, null, 0, 0, 0);

        int key = Integer.parseInt(repository.addUser(user, person));
        User tempUser = new User(key, user.getUserName(), user.getPassword(), user.getMail());
        Person tempPerson = new Person(key, person.getFirstName(), person.getLastName(), person.getPhoneNumber());
        users.add(tempUser);
        persons.add(tempPerson);
        session.setAttribute("user", tempUser);
        session.setAttribute("person", tempPerson);
        return new ModelAndView("userSession")
                .addObject("userSession", session)
                .addObject("user", tempUser)
                .addObject("person", tempPerson)
                .addObject("lunchBoxes", lunchBoxesJson)
                .addObject("lunchbox", lunchbox);

    }
//Logout-funktion.
    @PostMapping("/logout")
    public ModelAndView logout(HttpSession session, HttpServletRequest request) {

        session.removeAttribute("user");
        session.removeAttribute("person");
        request.getSession().invalidate();

        return new ModelAndView("redirect:/");
    }

//Skapa ny lunchbox utefter formulärets information
    @PostMapping("/lunchbox")
    public ModelAndView newLunchBox(LunchBox lunchbox, String image, String location, HttpSession session) throws SQLException {

        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyBTZQRmcgBi0Fw0rNCsKoUBZohWk7UW0dw&");
        GeocodingApiRequest req = GeocodingApi.newRequest(context).address(location);
        GeocodingResult[] results = req.awaitIgnoreError();
        for(GeocodingResult result : results) {
            BigDecimal lat = new BigDecimal(result.geometry.location.lat);
            lat = lat.setScale(6, RoundingMode.FLOOR);
            lat = lat.add(BigDecimal.valueOf( ((int)(Math.random() * (500+500)) -500)/(Math.pow(10,6))));
            lunchbox.setLatitud(lat);

            BigDecimal lng = new BigDecimal(result.geometry.location.lng);
            lng = lng.setScale(6, RoundingMode.FLOOR);
            lng = lng.add(BigDecimal.valueOf( ((int)(Math.random() * (500+500)) -500)/(Math.pow(10,6))));
            lunchbox.setLongitud(lng);
        }

        Person person = ((Person)session.getAttribute("person"));
        lunchbox.setPerson_ID(person.getPersonID());

        lunchbox.setLunchBoxID(repository.addLunchBox(lunchbox));
        lunchBoxes.add(lunchbox);
        lunchBoxesJson = objectToJSON(lunchBoxes);
        personJson = personToJSON(persons);
        newFood = true;


        return new ModelAndView("userSession")
                .addObject("lunchBoxes", lunchBoxesJson)
                .addObject("persons", personJson)
                .addObject("lunchbox", lunchbox)
                .addObject("location", location)
                .addObject("newFood", newFood);
    }


    @GetMapping("/settings")
    public ModelAndView settings(HttpSession session) {
        return new ModelAndView("settings")
                .addObject("userSession", session)
                .addObject("persons", personJson);
    }
    @PostMapping("/settings")
    public ModelAndView changeSettings(@Valid User user, BindingResult bru, @Valid Person person, BindingResult brp, @RequestParam String repeatedPassword, HttpSession session) throws Exception {

        if (bru.hasErrors() || brp.hasErrors() || userNameDuplicate(user) || confirmPassword(user.getPassword(), repeatedPassword) == false) {
            String error = "";
            if (brp.hasErrors()) {
                error = brp.getFieldError().getDefaultMessage();
            } else if (bru.hasErrors()) {
                error = bru.getFieldError().getDefaultMessage();
            }
            else if(confirmPassword(user.getPassword(), repeatedPassword) == false){
                error = "Lösenorden stämmer inte överrens";
            }
            return new ModelAndView("settings")
                    .addObject("error", error)
                    .addObject("persons", personJson);
        }

        user.setUserName(((User)session.getAttribute("user")).getUserName());
        user.setUserID(((User)session.getAttribute("user")).getUserID());
        person.setPersonID(((Person)session.getAttribute("person")).getPersonID());

        session.removeAttribute("user");
        session.removeAttribute("person");
        session.setAttribute("user", user);
        session.setAttribute("person", person);
        repository.updateUser(user, person);

        updateUserList(user);
        updatePersonList(person);
        updatedSettings = true;

        return new ModelAndView("settings")
                .addObject("userSession", session)
                .addObject("updatedSettings", updatedSettings)
                .addObject("persons", personJson);

    }

    //Visa alla lunchboxes tillhörande inloggad person
    @PostMapping("/updateLunchBoxes")
    public ModelAndView update(HttpSession session, LunchBox lunchbox) {

        Person person = (Person)session.getAttribute("person");
        ArrayList<LunchBox> personLunchBoxes = new ArrayList<>();

        for (int i = 0; i<lunchBoxes.size(); i++) {
            if (lunchBoxes.get(i).getPerson_ID() == person.getPersonID()){
                personLunchBoxes.add(lunchBoxes.get(i));
            }
        }

        return new ModelAndView("userLunchBoxes")
                .addObject("userSession", session)
                .addObject("personLunchBoxes", personLunchBoxes)
                .addObject("lunchbox", lunchbox);

    }

//Ta bort markerad lunchbox för inloggad person
    @PostMapping("/remove")
    public ModelAndView removeLunchBox(@RequestParam int lunchboxid, HttpSession session) throws SQLException{

        ArrayList<LunchBox> personLunchBoxes = new ArrayList<>();
        Person person =(Person)session.getAttribute("person");
        if(lunchboxid > 0) {
            repository.removeLunchBox(lunchboxid);
            for (int i = 0; i<lunchBoxes.size(); i++) {
                if(lunchBoxes.get(i).getLunchBoxID() == lunchboxid) {
                    lunchBoxes.remove(i);
                    for(LunchBox box : lunchBoxes){
                        System.out.println(box.getDescription());
                    }
                }
            }

            for (int i = 0; i<lunchBoxes.size(); i++) {
                if (lunchBoxes.get(i).getPerson_ID() == person.getPersonID()) {
                personLunchBoxes.add(lunchBoxes.get(i));
            }}
        }
        lunchBoxesJson = objectToJSON(lunchBoxes);

        return new ModelAndView("userLunchBoxes")
                .addObject("userSession", session)
                .addObject("personLunchBoxes", personLunchBoxes)
                .addObject("lunchBoxes", lunchBoxesJson);
    }
    @PostMapping("/decrease")
    public ModelAndView decreaseAmount(@RequestParam int lunchboxid, HttpSession session) throws SQLException {
        ArrayList<LunchBox> personLunchBoxes = new ArrayList<>();
        Person person =(Person)session.getAttribute("person");
        int currentAmount = 0;

        if(lunchboxid > 0) {
            for (int i = 0; i<lunchBoxes.size(); i++) {
                if(lunchBoxes.get(i).getLunchBoxID() == lunchboxid) {
                    currentAmount = lunchBoxes.get(i).getAmountOfLunchBoxes();
                    if(currentAmount>0) {
                        lunchBoxes.get(i).setAmountOfLunchBoxes(currentAmount - 1);
                    }
                }
            }
            if (currentAmount>0) {
                repository.decreaseAmount(lunchboxid, currentAmount);
            }
            for (int i = 0; i<lunchBoxes.size(); i++) {
                if (lunchBoxes.get(i).getPerson_ID() == person.getPersonID()) {
                personLunchBoxes.add(lunchBoxes.get(i));
            } }
        }
        lunchBoxesJson = objectToJSON(lunchBoxes);

        return new ModelAndView("userLunchBoxes")
                .addObject("userSession", session)
                .addObject("personLunchBoxes", personLunchBoxes)
                .addObject("lunchBoxes", lunchBoxesJson);
    }

// Funktioner

    public boolean userNameDuplicate(User user) {
        boolean duplicate = false;

        for(User index : users) {
            if(index.getUserName().equals(user.getUserName())){
                duplicate = true;
                return duplicate;
            }
        }return duplicate;
    }


    public String personToJSON(ArrayList<Person> array) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "[";

        for (int i = 0; i<array.size(); i++) {
            try {
                jsonInString += mapper.writeValueAsString(array.get(i));
                if (i<array.size()-1) {
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

    private Person returnCorrectPerson(int userId) {
        for(Person person : persons){
            if (person.getPersonID() == userId) {
                return person;
            }
        }return null;
    }

    //Playing around with matApi.se
    @GetMapping("/test")
    public ModelAndView update() {
        return null;
    }

    //Playing around with matApi.se
    @PostMapping("/test")
    public ModelAndView getApi(@RequestParam String ingredient) throws Exception{
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=10+Tulegatan,+Stockholm,+Sweden&key=AIzaSyBTZQRmcgBi0Fw0rNCsKoUBZohWk7UW0dw";
        String ingredientInfo = readUrl(url);
        System.out.println(ingredientInfo);
        boolean searchedForIngredient = true;

        return new ModelAndView("test")
                .addObject("ingridient", ingredientInfo)
                .addObject("searchedForIngredient", searchedForIngredient);

    }


    //Built if we want to use matapi.se
    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }


    private boolean confirmPassword(String password, String repeatPassword) {
        boolean confirmPassword = false;
        if(password.equals(repeatPassword)) {
            confirmPassword = true;
        }
        return confirmPassword;
    }

    private void updateUserList(User updatedUser){
        for(User user : users){
            if(updatedUser.getUserID() == user.getUserID()){
                user.setPassword(updatedUser.getPassword());
                user.setMail(updatedUser.getMail());
            }
        }
    }


    private void updatePersonList(Person updatedPerson){
        for(Person person : persons){
            if(updatedPerson.getPersonID() == person.getPersonID()){
                person.setFirstName(updatedPerson.getFirstName());
                person.setLastName(updatedPerson.getLastName());
                person.setPhoneNumber(updatedPerson.getPhoneNumber());
            }
        }
    }


}
