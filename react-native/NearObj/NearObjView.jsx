import React, { useState, useEffect, useContext } from 'react';
import { useFocusEffect } from '@react-navigation/native';
import { StyleSheet, Text, View, FlatList, TouchableOpacity, Image } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { LocationContext } from '../LocationContext';
import { useSQLiteContext } from 'expo-sqlite';
import CommunicationController from '../CommunicationComponents/CommunicationController';
import { getItemById, addItem } from '../Storage/sqliteDatabase';

export default function NearObjView({ navigation }) {

    const db = useSQLiteContext();

    const location = useContext(LocationContext);
    const [amuletLevel, setAmuletLevel] = useState(0)
    const [itemsList, setItemsList] = useState([]);

    // carica ogni volta che accediamo alla pagina
    useFocusEffect(
        React.useCallback(() => {
            
            if (location.lat === null || location.lon ===null) {
                    Alert.alert('caricamento posizione', 'Sto connettendo ai servizi di location, attendere...');
                } else {
                loadItems(location.lat, location.lon);
                }   
        }, [])
    );
    // carica Una volta
    useEffect(() => {
        loadUserAmulet();
    }, []);
    const loadUserAmulet = async () => {
        const sid = await AsyncStorage.getItem("SID")
        const uid = await AsyncStorage.getItem("UID")
        let serverData = await CommunicationController.getUserDataByUID(sid, uid)
        if (serverData.amulet === null){
            setAmuletLevel(0)
        }else{
            setAmuletLevel(serverData.amulet)
        }
    }
    
    const loadItems = async (lat,lon)=>{
        console.log('carica oggettini vicini!')
        const sid = await AsyncStorage.getItem("SID")
        let serverData = await CommunicationController.getItemsAround(sid, await lat, await lon)
        let fullArray = []
        let itemVicini =[]
        serverData.forEach(item => {
            if (distanceToItem(item.lat, item.lon, location.lat, location.lon) <= amuletLevel+100){
                itemVicini.push(item)
            }
        });
        //setItemsList(itemVicini)

        // inizio full caricamento

        itemVicini.forEach(async virtualObj => {// per ognuno dei  virtualObj

            let dbData = await getItemById(db, virtualObj.id)
            if (dbData === null || dbData === undefined) {
                let virtualObjData = await CommunicationController.getItemById(sid, virtualObj.id)
                addItem(virtualObjData, db)

                //aggiungiamo quando non ce nel db
                fullArray.push(virtualObjData)
                //console.log('- nuovo item: ', virtualObj.id)
            } else {

                    //console.log(' abbiamo già item - chill ', virtualObj.id)
                    //aggiungiamo quando ce e va bene cosi
                    fullArray.push(dbData)
                

            }
            let i = 0
            fullArray.forEach(element => {
                //console.log(i, "elemento del array enorne", element.id)
                i++
            });
            setItemsList(fullArray)

        });

        // fine full caricamento
    }
    
    const distanceToItem = (lat1, lon1, lat2, lon2)=> {
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
    


    useEffect(() => {
        loadItems(location.lat, location.lon);
    }, [location]);





    // Funzioni di generazione lista

    const renderItem = ({ item }) => (
        <View style={styles.row}>

            <View style={{ height: 50, width: 50, backgroundColor: '#000' }}><Image source={{ uri: ('data:image/jpeg;base64,' + item.image) }} style={{ height: 50, width: 50, backgroundColor: 'white', resizeMode: 'Image.resizeMode.contain', borderWidth: 0 }} /></View>


            <View style={styles.flexColumn}>
                <Text>id: {item.id}  -  tipo: {item.type} </Text>
                <Text>distanza in metri: {distanceToItem(item.lat, item.lon, location.lat, location.lon).toFixed(1)} </Text>
            </View>
            <TouchableOpacity style={styles.tastoitem} onPress={() => rowClickLoader(item.id)}><Text>Scopri!</Text></TouchableOpacity>

        </View>

    );



    const rowClickLoader = (idItem) => {
        if (idItem === null) {
            console.log('click on null!')
        } else {
            navigation.navigate('ActivateItem', idItem)
        }
    }


    return (
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
            <Text>Lista oggetti a portata di mano!</Text>
            <Text>lat: {location.lat}   lon: {location.lon}</Text>
            
        <FlatList
                style={styles.flatListStyle}
                data={itemsList}
                renderItem={renderItem}
                keyExtractor={(item) => item.id}
                contentContainerStyle={{ paddingBottom: 50 }}
            />


        </View>
    );
}



const styles = StyleSheet.create({
    container: {
        alignItems: 'center',
        margin: 'auto',
        height: '90%',
        width: '100%',

    },
    flatListStyle: {
        width: '100 %',
    },
    row: {
        padding: 5,
        flexDirection: 'row',
        justifyContent: 'space-between', // Spazia equamente le caselle
        alignItems: 'center',
        alignContent: 'center',
        textAlign: 'center',
        marginTop: 2,
        borderBottomWidth: 1,
        height: 80,
        width: '100 %',
        borderBottomColor: '#ccc',
        backgroundColor: '#ebebeb',
    },
    flexColumn: {
        flexDirection: 'column',
    },

    tastoitem: {
        height: 40,
        width: 140,
        backgroundColor: 'skyblue',
        justifyContent: 'center',
        alignItems: 'center',
        margin: 5
    },
});