import { StatusBar } from 'expo-status-bar';
import React, { useState, useEffect } from 'react';
import { StyleSheet, Text, View, TouchableOpacity, FlatList, Image } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

import { SQLiteProvider, useSQLiteContext } from 'expo-sqlite';
import { getUserByUid, addUser, replaceUser } from '../Storage/sqliteDatabase';
import CommunicationController from '../CommunicationComponents/CommunicationController'

//import { useNavigation } from '@react-navigation/native';



export default function RankedView({ navigation }) {

    const db = useSQLiteContext();
    //const [rankedList, setRankedList] = useState([]);
    const [fullList, setFullList] = useState([]);



    // CARICA SEMPRE - ad ogni uscita distrugge il Component e lo ricrea al click del tasto
    useEffect(() => {
        console.log('Caricamento RankedView')
        getRankedListFromServer()
    }, []);

    const getRankedListFromServer = async () => {
        let fullArray=[]
        const sid = await AsyncStorage.getItem("SID")
        //SERVER
        let serverData = await CommunicationController.getRankedlist(sid)
        //console.log(serverData[19].uid)
        //setRankedList(serverData)

        serverData.forEach(async player => {// per ognuno dei 20 player in ranked

            let dbData = await getUserByUid(db, player.uid)
            if (dbData === null || dbData === undefined) {
                let playerData = await CommunicationController.getUserDataByUID(sid, player.uid)
                addUser(playerData, db)

                //aggiungiamo quando non ce nel db
                fullArray.push(playerData)
                
                console.log('- dati chiesti per uid: ', player.uid)
            } else {
                if (player.profileversion != dbData.profileversion) {
                    console.log('profileversion diversi - richiesta dati ', player.uid)
                    let playerData = await CommunicationController.getUserDataByUID(sid, player.uid)
                    console.log('ATTENZIONE - STIAMO AGGIORNANDO IL PROFILO, FUNZIONE TESTATA POCHISSIMO, STARE ATTENTI AI LOG')
                    replaceUser(db, playerData)
                    
                    //aggiungiamo quando ce nel db MA è diverso
                    fullArray.push(playerData)
                } else {
                    console.log('profileversion uguali - chill ', player.uid)
                    //aggiungiamo quando ce e va bene cosi
                    fullArray.push(dbData)
                }

            }
            let i = 0
            fullArray.forEach(element => {
                console.log(i, "elemento del array enorne", element.uid)
                i++
            });
            setFullList(fullArray)
            //1. controlla nel DB

            // 1.1 controlla profileversion      
            // 1.2 show oppure replaceUser()

            // se non basta...

            // 1.1 chiedi al serveron
            // 1.2 salva con addUser()


        });
    }


    const renderItem = ({ item }) => (
        <View style={styles.row}>

            <View style={{ height: 50, width: 50, backgroundColor: '#000' }}><Image source={{ uri: ('data:image/jpeg;base64,' + item.picture) }} style={{ height:50, width: 50, backgroundColor: '#86a', resizeMode: 'Image.resizeMode.contain', borderWidth: 0 }} /></View>



            <View style={styles.flexColumn}>
                <Text>{item.name}  -  UID: {item.uid} </Text>
                <Text>HP: {item.life}     EXP: {item.experience} </Text>
            </View>

            <TouchableOpacity style={styles.tastovediprofilo} onPress={() => navigation.navigate('SeePlayer', item.uid)}><Text>Vedi profilo</Text></TouchableOpacity>

        </View>
    );

    return (
        <View style={styles.container}>
            <Text>Ranked Players Screen</Text>
            <FlatList
                style={styles.flatListStyle}
                data={fullList}
                renderItem={renderItem}
                keyExtractor={(item) => item.uid}
                contentContainerStyle={{ paddingBottom: 50 }}
            />

            <TouchableOpacity style={styles.tastoindietro} onPress={() => navigation.goBack()}><Text>Indietro</Text></TouchableOpacity>



        </View>
    );
}




const styles = StyleSheet.create({
    container: {
        alignItems:'center',
        margin: 'auto',
        height: '90%',
        width: '100%',

    },
    flatListStyle:{
        width:'100 %',
    },
    row: {
        padding:5,
        flexDirection: 'row',
        justifyContent: 'space-between', // Spazia equamente le caselle
        alignItems: 'center',
        alignContent:'center',
        textAlign:'center',
        marginTop: 2,
        borderBottomWidth: 1,
        height: 80,
        width: '100 %',
        borderBottomColor: '#ccc',
        backgroundColor:'#ebebeb',
    },
    flexColumn:{
        flexDirection: 'column',
    },

    tastoindietro: {
        height: 50,
        width: 200,
        backgroundColor: 'skyblue',
        justifyContent: 'center',
        alignItems: 'center',
        margin: 5
    },
    tastovediprofilo: {
        height: 20,
        width: 90,
        backgroundColor: 'skyblue',
        justifyContent: 'center',
        alignItems: 'center',
        margin: 5
    },
});