// main.js
var weatherMap = {
"sunny": ["Clear",  "Sunny", "Unknown Precipitation", "Unknown"],
"partlyCloudy":["Partly Cloudy", "Patches of Fog", "Shallow Fog", "Partial Fog",
"Light Fog", "Light Fog Patches", "Heavy Fog Patches"],
"veryCloudy": ["Heavy Fog", "Scattered Clouds"],
"overcast": ["Overcast", "Mostly Cloudy"],
"cold":
["Snow",
"Light Snow",
"Light Snow Grains",
"Light Ice Crystals",
"Light Ice Pellets",
"Light Hail",
"Light Low Drifting Snow",
"Light Blowing Snow",
"Light Snow Showers",
"Light Snow Blowing Snow Mist",
"Light Ice Pellet Showers",
"Light Hail Showers",
"Light Small Hail Showers",
"Light Thunderstorms and Snow",
"Light Thunderstorms and Ice Pellets",
"Light Thunderstorms with Hail",
"Light Thunderstorms with Small Hail",
"Light Freezing Drizzle",
"Light Freezing Rain",
"Light Freezing Fog",
"Heavy Snow",
"Heavy Snow Grains",
"Heavy Ice Crystals",
"Heavy Ice Pellets",
"Heavy Hail",
"Heavy Low Drifting Snow",
"Heavy Blowing Snow",
"Heavy Snow Showers",
"Heavy Snow Blowing Snow Mist",
"Heavy Ice Pellet Showers",
"Heavy Hail Showers",
"Heavy Small Hail Showers",
"Heavy Thunderstorms and Snow",
"Heavy Thunderstorms and Ice Pellets",
"Heavy Thunderstorms with Hail",
"Heavy Thunderstorms with Small Hail",
"Heavy Freezing Drizzle",
"Heavy Freezing Rain",
"Heavy Freezing Fog",
"Small Hail"],

"rain":
["Rain",
"Light Drizzle",
"Light Rain",
"Light Mist",
"Light Haze",
"Light Spray",
"Light Rain Mist",
"Light Rain Showers",
"Light Thunderstorm",
"Light Thunderstorms and Rain",
"Heavy Drizzle",
"Heavy Rain",
"Heavy Mist",
"Heavy Haze",
"Heavy Spray",
"Heavy Rain Mist",
"Heavy Rain Showers",
"Heavy Thunderstorm",
"Heavy Thunderstorms and Rain",
"Squalls"],

"windy":
["Windy",
"Heavy Smoke",
"Heavy Volcanic Ash",
"Heavy Widespread Dust",
"Heavy Sand",
"Heavy Dust Whirls",
"Heavy Sandstorm",
"Heavy Low Drifting Widespread Dust",
"Heavy Low Drifting Sand",
"Heavy Blowing Widespread Dust",
"Heavy Blowing Sand",
"Light Smoke",
"Light Volcanic Ash",
"Light Widespread Dust",
"Light Sand",
"Light Dust Whirls",
"Light Sandstorm",
"Light Low Drifting Widespread Dust",
"Light Low Drifting Sand",
"Light Blowing Widespread Dust",
"Light Blowing Sand",
"Funnel Cloud"]
}

var selectCity = document.querySelector(".js-select");
var container = document.querySelector(".js-container");

var city = document.querySelector(".js-city");
var country = document.querySelector(".js-country");
var condition = document.querySelector(".js-condition");
var tempF = document.querySelector(".js-tempF");
var tempC = document.querySelector(".js-tempC");
var windDir = document.querySelector(".js-wind-direction");
var windMPH = document.querySelector(".js-mph");
var windKPH = document.querySelector(".js-kph");
var visMi = document.querySelector(".js-vis-mi");
var visKm = document.querySelector(".js-vis-km");
var weatherIcon = document.querySelector(".js-weather-icon");
var backgroundImg = document.querySelector(".hero");

var insContainer = document.querySelector(".insurance-container");
var selectFiles = document.getElementById('fileInput');
var analyzingProgress = document.querySelector(".analyzing");
var choosenFileLabel = document.getElementById('choosenFileLabel');
var compContainer = document.getElementById('compContainer');

var warnMsg = document.getElementById('warningMessage');


selectCity.addEventListener("change", function(e){
    container.classList.remove("is-selected");
});


document.getElementById('myIframe').addEventListener("load", function(e){

    // Grab the exclusions from the iframe
    console.log("its loaded now");

    if (document.getElementById('myIframe').contentWindow.getExclusions() === undefined) {
        return;
    }

    //console.log("A: " + document.getElementById('myIframe').contentWindow.getExclusions());

    var obj = document.getElementById('myIframe').contentWindow.getExclusions();
    var resultsTable = document.getElementById('resultsTable');

    for (var eIndex in obj.exclusions) {

        var ex = obj.exclusions[eIndex].replace(/scuba/ig, '<span style="color: red">scuba</span>');

        console.log(ex);

        var divRow = document.createElement("div");
        divRow.className = "divTableRow";
        resultsTable.appendChild(divRow);

        var divCell = document.createElement("div");
        divCell.className = "divTableCell";
        divCell.innerHTML = ex;

        divRow.appendChild(divCell);

    }
    showResults();

});

function fileUploadStart() {
    // hide the comparison container when new file selected
    compContainer.classList.remove("ins-comp-visible");
    compContainer.style.display="none";

    // reset the width of the popup
    insContainer.style.width="530px";

    //show spinner
    analyzingProgress.style.display="block";
}

insuranceNav.style.display = "block";

function getUrlVars() {
    var vars = {};
    var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
        vars[key] = value;
    });
    return vars;
}

function loadOfflineJSON(callback) {
  var chosenCity = (selectCity && selectCity.value) ? selectCity.value: '';
  var xobj = new XMLHttpRequest();
  xobj.overrideMimeType("application/json");
  var cityJson = null;
  if (chosenCity === "Paris") cityJson = 'data/paris.json';
  else if (chosenCity === "Las Vegas") cityJson = 'data/nv.json';
  else if (chosenCity === "San Francisco") cityJson = 'data/sanfran.json';
  else if (chosenCity === "Miami") cityJson = 'data/miami.json';
  else if (chosenCity === "Cork") cityJson = 'data/cork.json';
  else if (chosenCity === "Barcelona") cityJson = 'data/barcelona.json';

  if (!xobj) { console.info('failed to create XMLHttpRequest object');}
  xobj.open("GET", cityJson, true);
  xobj.onreadystatechange = function() {
    if (xobj.readyState === 4) {
      if (xobj.status >= 200 && xobj.status < 300) {
        var res = (xobj.responseType === 'json') ? xobj.response : JSON.parse(xobj.responseText);
        callback(res);
      } else {
        callback({error: 'failed'});
      }
      xobj.onreadystatechange = null;
    }
  }
  xobj.send(null);
}

function updateForecast(data){
    if(data && data.current_observation){
        switchImages();
        var direction = data.current_observation.wind_dir;
        var windDirection = direction.charAt(0);
        city.innerHTML = data.current_observation.display_location.city;
        country.innerHTML = data.current_observation.display_location.state_name;
        condition.innerHTML = data.current_observation.weather;
        tempF.innerHTML = data.current_observation.temp_f;
        tempC.innerHTML = data.current_observation.temp_c;
        windDir.innerHTML = windDirection;
        windMPH.innerHTML = data.current_observation.wind_mph;
        windKPH.innerHTML = data.current_observation.wind_kph;
        visMi.innerHTML = data.current_observation.visibility_mi;
        visKm.innerHTML = data.current_observation.visibility_km;

        // close insurance stuff if its open
        closeIns();

        container.classList.add("is-selected");
        switchWeatherImages(data);
    }
}


function switchWeatherImages(data){
    var condition = data.current_observation.weather;
        if (weatherMap.partlyCloudy.indexOf(condition) > -1) {
                weatherIcon.src = "images/partly-cloudy.svg";
        } else if (weatherMap.veryCloudy.indexOf(condition) > -1) {
                weatherIcon.src = "images/very-cloudy.svg";
        } else if (weatherMap.overcast.indexOf(condition) > -1) {
                weatherIcon.src = "images/overcast.svg";
        } else if (weatherMap.cold.indexOf(condition) > -1) {
                weatherIcon.src = "images/cold.svg";
        } else if (weatherMap.rain.indexOf(condition) > -1) {
                weatherIcon.src = "images/rain.svg";
        } else {
            weatherIcon.src = "images/sunny.svg";
        }
}

function switchImages(){
    var chosenCity = (selectCity && selectCity.value) ? selectCity.value: '';
    switch(chosenCity) {
        case "Paris":
            backgroundImg.style.backgroundImage = "url('./images/paris.jpeg')";
            break;
        case "Las Vegas":
            backgroundImg.style.backgroundImage = "url('./images/vegas.jpeg')";
            break;
        case "San Francisco":
            backgroundImg.style.backgroundImage = "url('./images/sanfran.jpeg')";
            break;
        case "Miami":
            backgroundImg.style.backgroundImage = "url('./images/miami.jpeg')";
            break;
        case "Cork":
            backgroundImg.style.backgroundImage = "url('./images/cork.jpeg')";
            break;
        case "Barcelona":
            backgroundImg.style.backgroundImage = "url('./images/barcelona.jpeg')";
            break;
        default:
            backgroundImg.style.backgroundImage = "url('./images/default.jpeg')";
    }
}

function callRESTAPI() {
	var chosenCity = (selectCity && selectCity.value) ? selectCity.value: '';
	if (chosenCity.length === 0) {
		backgroundImg.style.backgroundImage = "url('./images/default.jpeg')";
		return
	}
	var resturl = null;
    var getUrl = window.location;

	var request = new XMLHttpRequest();
	if (chosenCity === "Paris"){
	request.open('GET', getUrl.protocol+ "//" + getUrl.hostname + ":" + getUrl.port + "/resorts/weather?selectedCity=Paris", true);
	}
	else if (chosenCity === "Las Vegas"){
	request.open('GET', getUrl.protocol+ "//" + getUrl.hostname + ":" + getUrl.port + "/resorts/weather?selectedCity=Las_Vegas", true);
	}
	else if (chosenCity === "San Francisco"){
	request.open('GET', getUrl.protocol+ "//" + getUrl.hostname + ":" + getUrl.port + "/resorts/weather?selectedCity=San_Francisco", true);
	}
	else if (chosenCity === "Miami"){
	request.open('GET', getUrl.protocol+ "//" + getUrl.hostname + ":" + getUrl.port + "/resorts/weather?selectedCity=Miami", true);
	}
	else if (chosenCity === "Cork"){
	request.open('GET', getUrl.protocol+ "//" + getUrl.hostname + ":" + getUrl.port + "/resorts/weather?selectedCity=Cork", true);
	}
	else if (chosenCity === "Barcelona"){
	request.open('GET', getUrl.protocol+ "//" + getUrl.hostname + ":" + getUrl.port + "/resorts/weather?selectedCity=Barcelona", true);
	}

	request.onload = function() {
		// Begin accessing JSON data here
		try{
			if (request.status != 500){
			var data = JSON.parse(this.response);
			updateForecast(data);
			}
		}catch (e){	}

		// If the liberty server has this 500 error, use cached data
		if (request.status == 500){
			loadOfflineJSON(function(result){
				if(result) updateForecast(result);
			});
		}
	};
	request.send();
}

/*
 * Simulate click on the file input element (which is hidden)
 */
function getFile() {
    document.getElementById('fileInput').click();
}

/*
 * Show the results table with benefit comparison
 */
function showResults() {
    analyzingProgress.style.display="none";
    warnMsg.style.display="block";

    insContainer.style.width="1200px";

    compContainer.classList.add("ins-comp-hidden");
    compContainer.style.display="block";

    setTimeout(function() {
        compContainer.classList.add("ins-comp-visible");
    }, 500)
}

function closeIns() {
    compContainer.style.display="none";
    compContainer.classList.remove("ins-comp-visible");

    warnMsg.style.display="block";
    insContainer.style.width="530px";
    insContainer.classList.remove("ins-is-selected");
}

function checkCover() {
    compContainer.style.display="none";
    compContainer.classList.remove("ins-comp-visible");

    warnMsg.style.display="none";
    container.classList.remove("is-selected");
    insContainer.classList.add("ins-is-selected");

}

function checkUploadResponse() {
    analyzingProgress.style.display="none";
}