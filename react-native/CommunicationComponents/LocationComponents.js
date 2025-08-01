import { StyleSheet, Text, View, ActivityIndicator } from 'react-native';
import React, { createContext, useState, useEffect } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import * as Location from 'expo-location';



// posizione
export async function getLocationPermission() {
    let canUseLocation = false;
    const grantedPermission = await Location.getForegroundPermissionsAsync()
    if (grantedPermission.status === "granted") {
        canUseLocation = true;
    } else {
        const permissionResponse = await Location.requestForegroundPermissionsAsync()
        if (permissionResponse.status === "granted") {
            canUseLocation = true;
        }
    }
    return canUseLocation;
}

/*
useEffect(() => {
    if (allowLocate) {
        locationPermissionAsync()
    }
}, [allowLocate]);
*/
/*
setAllowLocate(locationPermissionAsync());
*/

/*
Location.watchPositionAsync(
    {
        //timeInterval: 3000,  //Solo su Android
        accuracy: Location.Accuracy.High,
        distanceInterval: 30, // Distanza minima in metri tra ogni aggiornamento
    },
    (location) => {
        console.log("Posizione aggiornata: " + location.coords.latitude + " - " + location.coords.longitude);
        let x = { lat: location.coords.latitude, lon: location.coords.longitude };
        setLocation(x);
        //locationSimpleVAR = { lat: location.coords.latitude, lon: location.coords.longitude }

    }
);*/
