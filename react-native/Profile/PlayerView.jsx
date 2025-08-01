import { StatusBar } from 'expo-status-bar';
import React, { useState, useEffect } from 'react';
import { StyleSheet, Text, View, TouchableOpacity, Button, Image, Alert, Modal, TextInput, KeyboardAvoidingView, Platform } from 'react-native';
import * as ImagePicker from 'expo-image-picker';
import { SQLiteProvider, useSQLiteContext } from 'expo-sqlite';
import { useFocusEffect } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { getUserByUid } from '../Storage/sqliteDatabase';
import CommunicationController from '../CommunicationComponents/CommunicationController'
import * as ImageManipulator from 'expo-image-manipulator';
import { useRoute } from '@react-navigation/native';

export default function PlayerView({ navigation }) {

    const db = useSQLiteContext();
    const [playerData, setPlayerData] = useState({ name: '...', uid: 12345, life: 100, experience: 0, weapon: 0, armor: 0, amulet: 0, picture: '', Profileversion: 0, positionshare: false });
    const [infoPosizione, setInfoPosizione] = useState('il player non ha acconsentito alle informazioni sulla posizione')
    const [uriImageBase64, setUriImageBase64] = useState('data:image/jpeg;base64,');
    
    const route = useRoute();
    const PlayerUID = route.params; 


    // CARICA SEMPRE - ad ogni uscita distrugge il Component e lo ricrea al click del tasto
    useEffect( () => {
        //console.log('Caricamento PlayerView: ',PlayerUID)
        loadPlayerData(PlayerUID)
    }, []);

   

    const loadPlayerData = async (PlayerUID) => {
        
        let dbData = await getUserByUid(db, PlayerUID)
        setPlayerData(dbData)
        setUriImageBase64('data:image/jpeg;base64,' + dbData.picture)
        if(dbData.positionshare)
            setInfoPosizione('puoi trovarlo sulla mappa!!')
    }

    const valAlloggetto = (id) => {
        if (id === null) {
            console.log('hai cliccato su una casella vuota, l\'id selezionato è null')
            showAlertNoItem()
        } else {
            navigation.navigate('SeeVirtualObj', id)
        }
    }

    const showAlertNoItem = () => {
        Alert.alert('Slot vuoto!', 'Il giocatore non ha equipaggiato nulla in questo slot!');
    };

    const getPlaceholder =(type, idItem)=>{
        if(idItem === null){
            
            return require('../customIcon/noItem.png');
        }else{
            switch (type) {
                case 'weapon':
                    return require('../customIcon/weapon.png');
                    
                case 'armor':
                    return require('../customIcon/armor.png');
                    
                case 'amulet':
                    return require('../customIcon/amulet.png');
                    
                default:
                    return require('../customIcon/noItem.png');
                    
            }
        }

    }

    return (
        <View style={styles.container}>

            <View style={styles.imgBlock}>

          
                <View style={{ height: 200, width: 200, backgroundColor:'#000'}}><Image source={{ uri: uriImageBase64 }} style={{ height: 200, width: 200, backgroundColor: '#86a', resizeMode: 'Image.resizeMode.contain', borderWidth: 0 }} /></View>
                
            </View>
           
            <View style={styles.infoBlock}><Text>{playerData.name.name === null ? "vuoto" : playerData.name} - {playerData.uid}</Text>
                <View style={{ flexDirection: 'row' }}>
                    <Text style={{ padding: 5 }}>HP: {playerData.life}</Text>
                    <Text style={{ padding: 5 }}>EXP: {playerData.experience}</Text>
                </View>

                <Text style={{ padding: 5 }}>positionshare: {infoPosizione}</Text>
                
            </View>
            <View style={styles.itemsBlock}>
                <View style={styles.item}><TouchableOpacity style={styles.tastooggettini} onPress={() => valAlloggetto(playerData.weapon)} title="Ranekd"><Text>Visualizza</Text></TouchableOpacity>
                    <View style={{ height: 120, width: 120, justifyContent: 'center', alignItems: 'center' }}>
                        <Image source={getPlaceholder('weapon', playerData.weapon) } style={{ height: 100, width: 100, resizeMode: 'Image.resizeMode.contain', borderWidth: 0}} />
                    </View>
                </View>
                <View style={styles.item}><TouchableOpacity style={styles.tastooggettini} onPress={() => valAlloggetto(playerData.armor)} title="Ranekd"><Text>Visualizza</Text></TouchableOpacity>
                    <View style={{ height: 120, width: 120, justifyContent: 'center', alignItems: 'center' }}>
                        <Image source={getPlaceholder('armor', playerData.armor)} style={{ height: 100, width: 100, resizeMode: 'Image.resizeMode.contain', borderWidth: 0 }} />
                    </View>
                </View>
                <View style={styles.item}><TouchableOpacity style={styles.tastooggettini} onPress={() => valAlloggetto(playerData.amulet)} title="Ranekd"><Text>Visualizza</Text></TouchableOpacity>
                    <View style={{ height: 120, width: 120, justifyContent: 'center', alignItems: 'center' }}>
                        <Image source={getPlaceholder('amulet', playerData.amulet)} style={{ height: 100, width: 100, resizeMode: 'Image.resizeMode.contain', borderWidth: 0 }} />
                    </View>
                </View>
            </View>
            <View style={styles.buttonsBlock}>

                <TouchableOpacity style={styles.tastoimmagine} onPress={() => navigation.goBack()}><Text>Indietro</Text></TouchableOpacity>
                
            </View>
        </View>
    );


    

}





const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: 'white',
        alignItems: 'center',
        justifyContent: 'center',
    },
    imgBlock: {
        flex: 3,
        //backgroundColor: 'green',
        width: '100%',
        alignItems: 'center',
        justifyContent: 'center',
        padding: 10
    },
    infoBlock: {
        flex: 1.5,
        //backgroundColor: 'blue',
        alignItems: 'center',
        justifyContent: 'center',
        width: '100%'
    },
    itemsBlock: {
        flex: 2.5,
        //backgroundColor: 'orange',
        alignItems: 'center',
        justifyContent: 'center',
        width: '100%',
        flexDirection: 'row'
    },
    buttonsBlock: {
        flex: 1.5,
        //backgroundColor: 'yellow',
        alignItems: 'center',
        justifyContent: 'center',
        width: '100%'
    },

    image: {
        width: 200,
        height: 200,

    },
    tastoimmagine: {
        height: 50,
        width: 200,
        backgroundColor: 'skyblue',
        justifyContent: 'center',
        alignItems: 'center',
        margin: 5
    },
    item: {
        margin: 5,
        width: 120,
        height: 160,
        backgroundColor: 'gray'
    },
    tastooggettini: {
        height: 40,

        backgroundColor: 'skyblue',
        justifyContent: 'center',
        alignItems: 'center',
    },

    tastoindietro: {
        height: 50,
        width: 200,
        backgroundColor: 'skyblue',
        justifyContent: 'center',
        alignItems: 'center',
        margin: 5
    },
});