import { StatusBar } from 'expo-status-bar';
import React, { useState, useEffect } from 'react';
import { StyleSheet, Text, View, TouchableOpacity, Button, Image, Alert, Modal, TextInput, KeyboardAvoidingView, Platform } from 'react-native';

import { SQLiteProvider, useSQLiteContext } from 'expo-sqlite';
import { useFocusEffect } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { getItemById, addItem } from '../Storage/sqliteDatabase';
import CommunicationController from '../CommunicationComponents/CommunicationController'

import { useRoute } from '@react-navigation/native';

export default function VirtualObjView({ navigation }) {

    const db = useSQLiteContext();
    const [itemData, setItemData] = useState({ id: 12345, type: 'nulltype', level: 1, lat: 0, lon: 0,  image: '',name:'nullname' });
    
    const [textItem, setTextItem] = useState('caricamento Oggetto... attendere')
    const [uriImageBase64, setUriImageBase64] = useState('data:image/jpeg;base64,');

    const route = useRoute();
    const itemID = route.params;

    const strWeapon = 'Le armi permettono di sconfiggere i mostri subendo meno danni. I danni subiti sono diminuiti in una percentuale definita dal livello dell\'arma.Per esempio un\'arma di livello 20 permette di subire il 20 % in meno dei danni durante un combattimento.'
    const strArmor  = 'Le armature permettono di aumentare il numero massimo di punti vita. Per esempio con un\'armatura di livello 20 il numero massimo di punti vita è 120.'
    const strAmulet = 'Ogni Amuleto permette di aumentare la distanza alla quale gli oggetti sono considerati a portata di mano, per esempio un artefatto di tipo amuleto di livello 20 permette di raggiungere oggetti virtuali a 120m.'
    //const strCandy = 'Ogni caramella è caratterizzata da un Livello .All\'assaggio guadagni punti vita compresi tra il livello della caramella e il doppio!. Il valore è scelto casualmente.'
    //const strMonster = 'Un Mostro, spaccalo di bastonate per ottenre esperienza e diventare il migliore! Ogni mostro è caratterizzato dal suo livello.Quando combattiperdi punti vita compresi tra il suo livello e 2xlivello.Se perdi più punti vita di quelli che hai, crepi e questo significa che perdi tutti i punti esperienza e tutti gli oggetti equipaggiati.Se vinci guadagni punti esperienza pari al livello del mostro sconfitto.'


    // CARICA SEMPRE - ad ogni uscita distrugge il Component e lo ricrea al click del tasto
    useEffect(() => {
        
        loadItemData(itemID)
    }, []);


    const loadItemData = async (itemID) => {
        console.log('item con id pari a... ', itemID)
        let dbData = await getItemById(db, itemID)
        if(dbData === null){
            console.log('oggetto non precedentemente salvato, procedo a chiederlo')
            
            //SERVER
            const sid = await AsyncStorage.getItem("SID")
            let serverData = await CommunicationController.getItemById(sid, itemID)
            setItemData(serverData)
            loadImageAndText(serverData.image, serverData.type)
            
            // aggiunta al db...
            addItem(serverData,db)

        } else {
            setItemData(dbData)
            loadImageAndText(dbData.image, dbData.type)
        }

    }

    const loadImageAndText = async (image, type) =>{
        if (image === null) {
            console.log('null image')
        } else {
            setUriImageBase64('data:image/jpeg;base64,' + image)
        }

        switch (type) {
            case 'weapon':
                setTextItem(strWeapon)
                break;
            case 'armor':
                setTextItem(strArmor)
                break;
            case 'amulet':
                setTextItem(strAmulet)
                break;
            case 'candy':
                setTextItem(strCandy)
                break;
            case 'monster':
                setTextItem(strMonster)
                break;
            default:
                break;
        }
    }
    

    return (
        <View style={styles.container}>

            <View style={styles.imgBlock}>
                <View style={{ height: 350, width: 350, backgroundColor: '#403f3e' }}><Image source={{ uri: uriImageBase64 }} style={{ height: 350, width: 350, backgroundColor: '#403f3e', resizeMode: 'Image.resizeMode.contain', borderWidth: 0 }} /></View>
            </View>

            <View style={styles.infoBlock}>
                <Text>{itemData.name} - {itemData.id}</Text>
                <Text>Livello {itemData.level}</Text>
                <Text>Lat: {itemData.lat}  Lon: {itemData.lon}</Text>
                <Text style={{ padding: 5 }}>{textItem} </Text>
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