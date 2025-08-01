import { StyleSheet, Text, View, ActivityIndicator } from 'react-native';
import React, { createContext,useState, useEffect } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';

import NearObjStackScreen from  './NearObj/NearObjStackScreen';
import ProfileStackScreen from './Profile/ProfileStackScreen';
import MapStackScreen from './Map/MapStackScreen';

import { checkSID_UID } from './Storage/AsncStorageComponents';
import * as Location from 'expo-location';
import { getLocationPermission } from './CommunicationComponents/LocationComponents';

import { LocationContext } from './LocationContext'; // Importa il contesto lat-lon


//db
import { SQLiteProvider, useSQLiteContext } from 'expo-sqlite';
import { setupTables } from './Storage/sqliteDatabase'


//   Tab Navigator
const Tab = createBottomTabNavigator();


export default function App() {

    const[loading, setLoading] = useState(true);
    const [location, setLocation] = useState({ lat: null, long: null });
    const [allowLocate, setAllowLocate] = useState(false);
    var locationSimpleVAR = ({ lat: 0, lon: 0 });

    const [reloadData, setReloadData] = useState(false);



    //Prima cosa da fare check SID - UID
    // 1. sistemazione chiesta da JS - aiuto di chat per la costruzione - studiala bene
    // 2. startare il caricamento della posizione
    const siduidapp = async () => setLoading(!await checkSID_UID());
    useEffect( () => {
        siduidapp();
        testLocationPermission();
    }, []);
    // Check di Loading per disegnare icona del caricamento
    if (loading) {
        return (
            <View style={styles.container}>
                <ActivityIndicator size="large" color="#0000ff" />
            </View>
        );
    }

    async function testLocationPermission() {
        try {
            
            const canUseLocation = await getLocationPermission();
            console.log("Permesso per la posizione:", canUseLocation);
            if(canUseLocation){
                locazionefunzione()
            }
        } catch (error) {
            console.error("Errore durante la richiesta dei permessi:", error);
        }
    }
/*
    useEffect(() => {
        testLocationPermission();
    }, []);*/

    // Funzione che aggiorna periodicamente la posizione
    async function locazionefunzione() {
        await Location.watchPositionAsync(
            {
                accuracy: Location.Accuracy.High,
                distanceInterval: 30, // Distanza minima in metri tra ogni aggiornamento
            },
            (newLocation) => {
                const locationvar = {
                    lat: newLocation.coords.latitude,
                    lon: newLocation.coords.longitude
                };
                console.log("Posizione aggiornata: " + locationvar.lat + " - " + locationvar.lon);

                // Aggiorna lo stato della posizione
                setLocation(locationvar);
            }
        );
    }

    


  return (
    <LocationContext.Provider value={location}>
    

          <SQLiteProvider databaseName='pocketMonsterDatabase.db' onInit={setupTables}>

      <NavigationContainer>
                      <Tab.Navigator initialRouteName="Map">
                        <Tab.Screen name="Profile" component={ProfileStackScreen} />
                      <Tab.Screen name="Map" component={MapStackScreen} />
                        <Tab.Screen name="Items" component={NearObjStackScreen} />
                      </Tab.Navigator>
      </NavigationContainer>

          </SQLiteProvider>

    
    </LocationContext.Provider>
  );
}


const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
