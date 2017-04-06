
var geocoder;
var map;
var activeInfoWindow;
var activeMarker;
var markerList = [];
var infoWindowList = [];
var markerListCluster = [];
var persons = "";
var markerCluster;

/*if (location.protocol != 'https:')
{
    location.href = 'https:' + window.location.href.substring(window.location.protocol.length);
}*/

//initMap körs automatiskt när sidan laddas med hjälp av "async defer" i .html
function initMap() {
    var stockholm = {lat: 59.3293235, lng: 18.0685808};
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 12,
        center: stockholm,
        styles: mapStyle,
        disableDefaultUI: true,
        zoomControl: true
    });
    //Hämta nuvarande position
    navigator.geolocation.getCurrentPosition(success, error, options);


    function createMarker(pos, pers) {
        var icon = {url: 'icon/standard.png', scaledSize: new google.maps.Size(48, 48)};
        var iconDesc = "";
        var bgColor = 'grey';
        var imageurl = "";
        if(pos.vego){
            icon.url = 'icon/vego.png';
            imageurl = icon.url;
            iconDesc = 'Vegetarisk';
            bgColor = '#009933';
        };
        if(pos.vegan){
            icon.url = 'icon/vegan.png';
            imageurl = icon.url;
            iconDesc = 'Vegansk';
            bgColor = '#33ff66';
        };
        if(pos.kyckling){
            icon.url = 'icon/kyckling.png';
            imageurl = icon.url;
            iconDesc = 'Kyckling';
            bgColor = '#ffff4e';
        };
        if(pos.not){
            icon.url = 'icon/kött.png';
            imageurl = icon.url;
            iconDesc = 'Nötkött';
            bgColor = 'red';
        };
        if(pos.flask){
            icon.url = 'icon/fläsk.png';
            imageurl = icon.url;
            iconDesc = 'Fläsk';
            bgColor = '#ff9933';
        };
        if(pos.fisk){
            icon.url = 'icon/fisk.png';
            imageurl = icon.url;
            iconDesc = 'Fisk';
            bgColor = '#3399ff';
        };
        if(pos.image.length > 80){
            imageurl = 'data:image/png;base64,'+pos.image;
        }
        var marker = new google.maps.Marker({
            position: {lat: pos.latitud, lng: pos.longitud},
            map: map,  // google.maps.Map
            title: pos.description,
            icon: icon
        });
        var loggedIn = '<div id="infoWindowDiv">'+"Logga in för att se mer information"+'</div>';
        if(pers != ""){
            loggedIn = '<p>Pris: '+pos.pris+'kr</p><p>Säljare: '+pers.firstName+'</p><p>Telefonnummer: 0'+pers.phoneNumber+'</p>';
        }
        var infowindow = new google.maps.InfoWindow({
            content: '<div class="infoWindow"><div><h1>'+pos.description+'</h1>' +
            '<p>'+iconDesc+'</p>' +
            '<p>Beskrivning: '+pos.ingridiences+'</p>' +
            loggedIn +
            '</div><img src="'+imageurl+'"></div>',
            bgColor: bgColor
        });

        //Styling för infowindow
        google.maps.event.addListener(infowindow, 'domready', function() {
            if(activeInfoWindow != null || activeInfoWindow == infowindow){
                try{
                    activeInfoWindow.close();
                }catch(err){

                }
            }
            // Reference to the DIV which receives the contents of the infowindow using jQuery
            var iwOuter = document.querySelector('.gm-style-iw');

            /* The DIV we want to change is above the .gm-style-iw DIV.
             * So, we use jQuery and create a iwBackground variable,
             * and took advantage of the existing reference to .gm-style-iw for the previous DIV with .prev().
             */
            var iwBackground = iwOuter.previousElementSibling;

            // Remove the background shadow DIV
            iwOuter.parentElement.children[0].style.display = 'none';

            // Remove the white background DIV
            iwBackground.children[3].style.display = 'none';

            document.querySelector('.gm-style-iw').style.border = '7px solid ' + infowindow.bgColor;


            // Fixa stäng-knappen
            iwOuter.parentElement.children[2].style.top = '24%';
            iwOuter.parentElement.children[2].style.right = '8.9%';
            iwOuter.parentElement.children[2].style.height = '20px';
            iwOuter.parentElement.children[2].style.lineHeight = '20px';
            iwOuter.parentElement.children[2].style.fontSize = '20px';
            iwOuter.parentElement.children[2].style.backgroundColor = 'white';
            iwOuter.parentElement.children[2].style.width = '19px';
            iwOuter.parentElement.children[2].style.border =  '4px solid ' + infowindow.bgColor;
            iwOuter.parentElement.children[2].style.borderRadius = '25px';
            iwOuter.parentElement.children[2].style.opacity = '1';
            iwOuter.parentElement.children[2].style.textAlign = 'center';

            // Byta ut till ett kryss
            iwOuter.parentElement.children[2].innerHTML = 'X';
            // Osynlig hitbox
            iwOuter.parentElement.children[3].style.top = '22%';
            iwOuter.parentElement.children[3].style.right = '8%';
            activeInfoWindow = infowindow;
        });
        //Event-listener för on close click
        google.maps.event.addListener(infowindow,'closeclick',function(){
            activeMarker = "";
            activeInfoWindow = "";
            });
        marker.addListener('click', function() {
            if(activeMarker == this){

            }
            else {
                map.panTo(this.getPosition());
                activeMarker = this;
                infowindow.open(map, marker);
                setTimeout(function() { map.panBy(-10,-60)}, 700);

            }
        });
        infoWindowList.push(infowindow);
        markerList.push(marker);

        if(markerListCluster.length == 0) {
            markerListCluster = markerList;
            console.log("Edited markerListCluser with length: "+markerListCluster.length)
        }
        //google.maps.event.addDomListener(window, 'load', initialize);
        return marker;
    }
    function createMarkers() {
        if(persons.length > 0) {
            for (var i = 0; i < lunchBoxes.length; i++) {
                for (var j=0; j < persons.length; j++){
                    if(lunchBoxes[i].person_ID == persons[j].personID){
                        createMarker(lunchBoxes[i], persons[j]);
                        break;
                    }
                }
            }
        }
        else {
            for (var i = 0; i < lunchBoxes.length; i++) {
                createMarker(lunchBoxes[i], persons);
            }
        }

    }
    createMarkers();
    initialize();
}


//Funktion som letar upp koordinater för addressen som anges i textrutan och sätter ut pin

document.querySelector('.address').addEventListener('keyup', function(event) {
    if (event.keyCode == 13) {
        codeAddress(geocoder, map);
    }}
);
function codeAddress() {

    geocoder = new google.maps.Geocoder();

    var address = document.querySelector('.address').value;
    geocoder.geocode( {"address": address}, function(results, status) {

        if(status == "OK") {
            map.setCenter(results[0].geometry.location);

            //Om ingen träff på addressen
        } else {
            alert("Geocode not successful because: " + status);
        }
    });
}


//funktion som mekar med mat-apit'
function foodApi() {
    var ingridients;
    ingridients.push(ingridientInfo);
    console.log(ingridients);
}

//Hämta nuvarande position
var options = {
    enableHighAccuracy: true,
    timeout: 5000,
    maximumAge: 5000
};
function success(pos) {
    var crd = pos.coords;
    uluru = { lat: crd.latitude, lng: crd.longitude };
    map.setCenter(uluru);
    console.log('Your current position is:');
    console.log('Latitude: '+crd.latitude);
    console.log('Longitude: '+crd.longitude);
    console.log('More or less '+crd.accuracy+' meters.');
};

function error(err) {
    console.warn('ERROR(${err.code}): ${err.message}');
};
function filterMap(input){
    markerCluster.clearMarkers();
    markerListCluster=[];
    for(var i=0; i<markerList.length; i++){
        if(input.length==0){
            markerListCluster.push(markerList[i]);
            markerList[i].setVisible(true);
        }
        else if(infoWindowList[i].content.toLowerCase().includes(input.toLowerCase())){
            markerListCluster.push(markerList[i]);
            markerList[i].setVisible(true);
        }
        else{
            markerList[i].setVisible(false);
        }
    }
    initialize();

}
//Grejer för clusterermarker.js
function initialize(){
    markerCluster = new MarkerClusterer(map, markerListCluster, {imagePath: 'icon/m'});
}