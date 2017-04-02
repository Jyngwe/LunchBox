
var geocoder;
var map;

//initMap körs automatiskt när sidan laddas med hjälp av "async defer" i .html
function initMap() {
    var stockholm = {lat: 59.3293235, lng: 18.0685808};
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 12,
        center: stockholm,
        styles: mapStyle
    });
    var marker = new google.maps.Marker({
        animation: google.maps.Animation.DROP,
        position: stockholm,
        map: map
    });

    document.querySelector('.newLoc').addEventListener('click', function() {
        codeAddress(geocoder, map);
    });
    function createMarker(pos) {
        var marker = new google.maps.Marker({
            position: {lat: pos.latitud, lng: pos.longitud},
            map: map,  // google.maps.Map
            title: pos.description
        });

        var infowindow = new google.maps.InfoWindow({
            content: '<div>'+pos.description+'</div>'
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
    console.log(lunchBox);
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

