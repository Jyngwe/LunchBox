/**
 * Created by Administrator on 2017-03-29.
 */
function ready(annonsfunk) {
    if (document.readyState != 'loading') {
        annonsfunk();
    }else{
        document.addEventListener('DOMContentLoaded', annonsfunk)
    }
}
function annonsfunk() {
    var element =  document.querySelector(".annons");
    element.style.visibility = 'visible';
    var element2 =  document.querySelector('.black_overlay');
    element2.style.visibility = 'visible';
}
function closeannonsfunk(){
    var element =  document.querySelector(".annons");
    element.style.visibility = 'hidden';
    document.querySelector('.black_overlay').style.visibility = 'hidden';


}

function hej(bgfunk) {
    if (document.readyState != 'loading') {
        bgfunk();
    } else {
        document.addEventListener('DOMContentLoaded', bgfunk)
    }
}
function bgfunk() {
    var elementt =  document.querySelector("#bg");
    elementt.style.visibility = 'visible';
  var elementt2 =  document.querySelector(".black_overlay");
    elementt2.style.visibility = 'visible';
}
function closebgfunk(){
    var element =  document.querySelector("#bg");
    element.style.visibility = 'hidden';
    document.querySelector('.black_overlay').style.visibility = 'hidden';
}

function ready(kontaktaossfunk) {
    if (document.readyState != 'loading') {
        kontaktaossfunk();
    }else{
        document.addEventListener('DOMContentLoaded', kontaktaossfunk)
    }
}
function kontaktaossfunk() {
    var element =  document.querySelector("#kontaktaoss");
    element.style.visibility = 'visible';
    var element2 =  document.querySelector('.black_overlay');
    element2.style.visibility = 'visible';
}
function closekontaktaossfunk(){
    var element =  document.querySelector("#kontaktaoss");
    element.style.visibility = 'hidden';
    document.querySelector('.black_overlay').style.visibility = 'hidden';
    }

function ready(faqfunk) {
    if (document.readyState != 'loading') {
        faqfunk();
    }else{
        document.addEventListener('DOMContentLoaded', faqfunk)
    }
}
function faqfunk() {
    var element =  document.querySelector("#faq");
    element.style.visibility = 'visible';
    var element2 =  document.querySelector('.black_overlay');
    element2.style.visibility = 'visible';
}
function closefaqfunk(){
    var element =  document.querySelector("#faq");
    element.style.visibility = 'hidden';
    document.querySelector('.black_overlay').style.visibility = 'hidden';
    }

function ready(omossfunk) {
    if (document.readyState != 'loading') {
        omossfunk();
    }else{
        document.addEventListener('DOMContentLoaded', omossfunk)
    }
}
function omossfunk() {
    var element =  document.querySelector("#omoss");
    element.style.visibility = 'visible';
    var element2 =  document.querySelector('.black_overlay');
    element2.style.visibility = 'visible';
}
function closeomossfunk(){
    var element =  document.querySelector("#omoss");
    element.style.visibility = 'hidden';
    document.querySelector('.black_overlay').style.visibility = 'hidden';

}
function hej(popupfunk) {
    if (document.readyState != 'loading') {
        popupfunk();
    } else {
        document.addEventListener('DOMContentLoaded', popupfunk)
    }
}

function popupfunk() {
    var elementt = document.querySelector("#Popup");
    elementt.style.visibility = 'visible';
}

function ready(showSalesForm) {
    if (document.readyState != 'loading') {
        showSalesForm();
    } else {
        document.addEventListener('DOMContentLoaded', showSalesForm)
    }
}
function showSalesForm() {
    var elementt =  document.querySelector(".annons");
    elementt.style.visibility = 'visible';
/*        var elementt2 =  document.querySelector(".black_overlay");
     elementt2.style.visibility = 'visible';*/
}
function closeSalesForm(){
    var element =  document.querySelector(".annons");
    element.style.visibility = 'hidden';
    document.querySelector('.black_overlay').style.visibility = 'hidden';
}
function showBuyerInfo() {
    var element = document.querySelector("#buyerinfo");
    element.style.visibility='visible';
}
function closeBuyerInfo() {
    var element = document.querySelector("#buyerinfo");
    element.style.visibility='hidden';
}
function showNewFood() {
    var element = document.querySelector("#newFood");
    element.style.visibility='visible';
}
function closeNewFood() {
    var element = document.querySelector("#newFood");
    element.style.visibility='hidden';
}
function showUpdatedSettings() {
    var element = document.querySelector("#updatedSettings");
    element.style.visibility='visible';
}
function closeUpdatedSettings() {
    var element = document.querySelector("#updatedSettings");
    element.style.visibility='hidden';
}
