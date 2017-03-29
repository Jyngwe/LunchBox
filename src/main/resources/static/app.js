
var geocoder;
var map;

//initMap körs automatiskt när sidan laddas med hjälp av "async defer" i .html
function initMap() {
    console.log("InitMap");
    var uluru = {lat: 59.3293235, lng: 18.0685808};
    console.log(uluru);
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 12,
        center: uluru,
        styles: mapStyle
    });
    var marker = new google.maps.Marker({
        animation: google.maps.Animation.DROP,
        position: uluru,
        map: map
    });

    document.getElementById('submit').addEventListener('click', function() {
        codeAddress(geocoder, map);
    });

}

//Funktion som letar upp koordinater för addressen som anges i textrutan och sätter ut pin
function codeAddress() {
    geocoder = new google.maps.Geocoder();

    var address = document.getElementById('address').value;
    geocoder.geocode( {"address": address}, function(results, status) {

        if(status == "OK") {
            map.setCenter(results[0].geometry.location);
            var marker = new google.maps.Marker({
                map: map,
                position: results[0].geometry.location

            });
            //Om ingen träff på addressen
        } else {
            alert("Geocode not successful because: " + status);
        }
    });
}

