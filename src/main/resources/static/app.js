
var geocoder;
var map;
var activeInfoWindow;

//initMap körs automatiskt när sidan laddas med hjälp av "async defer" i .html
function initMap() {
    var stockholm = {lat: 59.3293235, lng: 18.0685808};
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 12,
        center: stockholm,
        styles: mapStyle
    });

/*    document.querySelector('.newLoc').addEventListener('click', function() {
        codeAddress(geocoder, map);
    });*/
    function createMarker(pos) {
        var icon = {url: 'icon/standard.png', scaledSize: new google.maps.Size(48, 48)};
        var iconDesc = "";
        var bgColor = 'grey';
        if(pos.vego){
            icon.url = 'icon/vego.png';
            iconDesc = 'Vegetarisk';
            bgColor = '#009933';
        };
        if(pos.vegan){
            icon.url = 'icon/vegan.png';
            iconDesc = 'Vegansk';
            bgColor = '#33ff66';
        };
        if(pos.kyckling){
            icon.url = 'icon/kyckling.png';
            iconDesc = 'Kyckling';
            bgColor = '#ffff4e';
        };
        if(pos.not){
            icon.url = 'icon/kött.png';
            iconDesc = 'Nötkött';
            bgColor = 'red';
        };
        if(pos.flask){
            icon.url = 'icon/fläsk.png';
            iconDesc = 'Fläsk';
            bgColor = '#ff9933';
        };
        if(pos.fisk){
            icon.url = 'icon/fisk.png';
            iconDesc = 'Fisk';
            bgColor = '#3399ff';
        };
        var marker = new google.maps.Marker({
            position: {lat: pos.latitud, lng: pos.longitud},
            map: map,  // google.maps.Map
            title: pos.description,
            icon: icon
        });

        var infowindow = new google.maps.InfoWindow({
            content: '<div class="infoWindow"><div><h1>'+pos.description+'</h1>' +
            '<p>'+iconDesc+'</p>' +
            '<p>Ingredienser'+pos.ingridiences+'</p>' +
            '</div><img src="'+icon.url+'"></div>',
            bgColor: bgColor
        });


        google.maps.event.addListener(infowindow, 'domready', function() {
            if(activeInfoWindow != null){
                activeInfoWindow.close();
            }
            activeInfoWindow = infowindow;
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
            iwOuter.parentElement.children[2].style.top = '-4.3%';
            iwOuter.parentElement.children[2].style.right = '8.7%';
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
            iwOuter.parentElement.children[3].style.top = '-7%';
            iwOuter.parentElement.children[3].style.right = '8%';



        });
        marker.addListener('click', function() {
            infowindow.open(map, marker);
        });

        return marker;
    }
    function createMarkers() {
        for (var i = 0; i < lunchBoxes.length; i++) {
            createMarker(lunchBoxes[i]);
        }
    }

    createMarkers();
}


//Funktion som letar upp koordinater för addressen som anges i textrutan och sätter ut pin
function codeAddress() {

    geocoder = new google.maps.Geocoder();

    var address = document.querySelector('.address').value;
    geocoder.geocode( {"address": address}, function(results, status) {

        if(status == "OK") {
            map.setCenter(results[0].geometry.location);
            var marker = new google.maps.Marker({
                map: map,
                position: results[0].geometry.location,


            });

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
    maximumAge: 30000
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



