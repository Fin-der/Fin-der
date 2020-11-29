// helper functions for keeping lng and lat values within their range
const wrapLng = (lng) => {
    var result = lng;
    if (lng > 180) {
        result = lng - 360;
    } else if (lng < -180) {
        result = lng + 360;
    }
};
const wrapLat = (lat) => {
    var result = lat;
    if (lat > 90) {
        result = 180 - lat;
    } else if (lat < -90) {
        result = lat + 180;
    }
    return result;
};

const calcProxQuery = (user, query) => {
    if (typeof user.preferences.proximity !== "undefined") {
        // Numbers and Formulae from 
        // https://stackoverflow.com/questions/1253499/simple-calculations-for-working-with-lat-lon-and-km-distance
        const latKmPerDeg = 110.574;
        const lngKmPerDeg = 111.32;
        const latProximityDeg = user.preferences.proximity / latKmPerDeg;
        const lngProximityDeg = user.preferences.proximity / (lngKmPerDeg * Math.cos(user.geoLocation.lat * Math.PI / 180));
        query.geoLocation = new Object();
        query.geoLocation.lng = { 
            $gt: wrapLng(user.geoLocation.lng - lngProximityDeg),
            $lt: wrapLng(user.geoLocation.lng + lngProximityDeg)
        };
        query.geoLocation.lat = {
            $gt: wrapLat(user.geoLocation.lat - latProximityDeg),
            $lt: wrapLat(user.geoLocation.lat + latProximityDeg)
        };
    }
};
const calcQuery = (user, query) => {
    if (typeof user.preferences.gender !== "undefined" && user.preferences.gender !== "All") {
        query.gender = user.preferences.gender;
    }
    if (typeof user.preferences.ageRange !== "undefined") {
        query.age = {$gt: user.preferences.ageRange.min, $lt: user.preferences.ageRange.max};
    }
    calcProxQuery(user, query);
};

export default calcQuery;