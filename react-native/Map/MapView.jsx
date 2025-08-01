import React, { useState, useEffect,useContext } from 'react';
import { StyleSheet, Text, View } from 'react-native';
import ReactNativeMapView, { Marker }  from 'react-native-maps';
import { LocationContext } from '../LocationContext';
import CommunicationController from '../CommunicationComponents/CommunicationController';
import AsyncStorage from '@react-native-async-storage/async-storage';

export default function MapView({ navigation }) {

    const [data, setData] = useState('datiDiProva_Map');
    const location  = useContext(LocationContext);

    const [positionPlayer, setPositionPlayer] = useState({
        latitude: 45,
        longitude: 9,
        latitudeDelta: 0.0017,
        longitudeDelta: 0.0017,
    });

    const[itemMarkers, setItemMarkers]=useState([])
    const [playerMarkers, setPlayerMarkers ] = useState([])


    const loadItemAndPlayers = async (lat,lon) => { 
        
            console.log('loadItemAndPlayers called - 10s')
            const sid = await AsyncStorage.getItem("SID")
            const uid = await AsyncStorage.getItem("UID")
            try {
                //chiami items (id: lat: lon: type: )
                let serverDataItems = await CommunicationController.getItemsAround(sid, lat, lon)
                setItemMarkers(serverDataItems)
                //chiami players (ci interessa... uid: lat: lon: )
                let serverDataPlayers = await CommunicationController.getPlayersAround(sid, lat, lon)
                setPlayerMarkers(serverDataPlayers)
            } catch (error) {
                console.log('loadItemAndPlayers error: qualcosa è andato storto: ',error);    
            } 
        
        
    }
        // load items
        
    useEffect(() => {
        if(location.lat == null || location.lon == null){
            console.log('location.lat == null || location.lon == null')
        }else{
            console.log('popipopi posto cambiato')
        setPositionPlayer({ latitude: location.lat, longitude: location.lon, latitudeDelta: 0.0017, longitudeDelta: 0.0017 })
            loadItemAndPlayers(location.lat, location.lon)
        }
        
    },[location]);
        

// scelta del marker - sicura con altri eventi
    const getMarkerImage = (type) => {
        switch (type) {
            case 'monster':
                return require('../customMarkers/marker_monster.png');
            case 'amulet':
                return require('../customMarkers/marker_item.png');
            case 'armor':
                return require('../customMarkers/marker_item.png');
            case 'weapon':
                return require('../customMarkers/marker_item.png');
            case 'candy':
                return require('../customMarkers/marker_candy.png');
            default:
                return require('../customMarkers/marker_blank.png');
        }
    }

    const goToItemClicked =(item,playerLat,playerLon)=>{
        if (item.id === null) {
            console.log('Errore: hai cliccato su un oggetto di ID null ... ')
        } else {
            if (distanceToItem(item.lat, item.lon, playerLat, playerLon) <= 100){
               navigation.navigate('ActivateItem', item.id)
            }else{
                console.log ('tropo lontano !')
            }

        }
    }
    const goToPlayerClicked = (playerUID) => {
        if (playerUID === null) {
            console.log('Errore: hai cliccato su un tizio di UID null ... ')
        } else {
            navigation.navigate('SeePlayerOnMap', playerUID)
        }
    }



    const distanceToItem = (lat1, lon1, lat2, lon2) => {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            let radlat1 = Math.PI * lat1 / 180;
            let radlat2 = Math.PI * lat2 / 180;
            let theta = lon1 - lon2;
            let radtheta = Math.PI * theta / 180;
            let dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
            if (dist > 1) {
                dist = 1;
            }
            dist = Math.acos(dist);
            dist = dist * 180 / Math.PI;
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344 * 1000

            return dist;
        }
    }



    return (
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>

            <ReactNativeMapView style={styles.map}
                scrollEnabled={true}
                zoomEnabled={true}
                rotateEnabled={true}
                pitchEnabled={true}
                region={positionPlayer} 
                onRegionChangeComplete={(region, gesture) => {
                    if (!gesture.isGesture) {
                        return
                    }}
                }
            >
                
                {itemMarkers.map(marker => (
                    <Marker
                        key={marker.id}
                        coordinate={{ latitude: marker.lat, longitude: marker.lon }}
                        title={`There's a ${marker.type} here!`}
                        image={getMarkerImage(marker.type)} // Usa un'immagine personalizzata per il marker
                        onPress={() => { goToItemClicked(marker,location.lat,location.lon) }}
                    >
                    </Marker>
                ))}
                {/*

ATTUALMENTE I PLAYER SONO DISATTIVATI PERCHé CE N'ERARNO TROPPI IN MEZZO 
                
                {playerMarkers.map(marker => (
                    <Marker
                        key={marker.uid}
                        coordinate={{ latitude: marker.lat, longitude: marker.lon }}
                        title={`There's a player here!`}
                        image={require('../customMarkers/marker_players.png')} // Usa un'immagine personalizzata per il marker
                        onPress={() => { goToPlayerClicked(marker.uid) }}
                    >
                    </Marker>
                ))}
                
                */}

                
                {/*player TU marker*/}
                <Marker
                    key={0}
                    coordinate={{ latitude: positionPlayer.latitude, longitude: positionPlayer.longitude }}
                    title={`TU!`}
                    image={require('../customMarkers/marker_me.png')} // Usa un'immagine personalizzata per il marker

                >
                </Marker>
            </ReactNativeMapView>            
        </View>
    );
}


const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#fff',
        alignItems: 'center',
        justifyContent: 'center',
    },
    map: {
        width: '100%',
        height: '100%',
    },
});
