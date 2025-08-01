import { StatusBar } from 'expo-status-bar';
import React, { useState, useEffect } from 'react';
import { StyleSheet, Text, View, TouchableOpacity, Button, Image, Alert, Modal, TextInput, KeyboardAvoidingView, Platform } from 'react-native';
import * as ImagePicker from 'expo-image-picker';
import { SQLiteProvider, useSQLiteContext } from 'expo-sqlite';
import { useFocusEffect } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { getUserByUid, getAllUsers, deleteAllUsers } from '../Storage/sqliteDatabase';
import CommunicationController from '../CommunicationComponents/CommunicationController'
import * as ImageManipulator from 'expo-image-manipulator';


export default function ProfileView({ navigation }) {
    
    const db = useSQLiteContext(); 
    const [userData, setUserData] = useState({ name: '...', uid: 12345, life: 100, experience: 0, weapon: 0, armor: 0, amulet: 0, picture: '',profileversion:-1,positionshare:false});
    const [uriImageBase64, setUriImageBase64] = useState('data:image/jpeg;base64,');
    
    const [modalVisible, setModalVisible] = useState(false);
    const [nameTextInput, setNameTextInput] = useState('');
    const [posiziones, setPosiziones] = useState(false);
    


    // Cose al Caricamento del Component
    useFocusEffect(
        React.useCallback( () => {
            console.log('carica dati profilo!')
            chechUpdateUserData()
            //loadUserData(); //-funziona
        }, [])
    );
    // Solo al primo caricamento del component e della app
    useEffect(() => {
        console.log('Primo load...')
        loadUserData(); //-funziona
    }, []);

    // CANCELLA TUTTO L'ASYNC STORAGE!
    const clearAsyncStorage = async () => {
        console.log('cancella tutto ASYNC STORAGE...');
        try {
            await AsyncStorage.clear();
            console.log('Storage cleared successfully');
        } catch (e) {
            console.error('Failed to clear the async storage:', e);
        }
    };

    // CANCELLA TUTTO IL DATABASE!
    const clearDBStorage = async () => {
        console.log('cancella tutto DATABASE...');
        try {
            
            deleteAllUsers(db)
            console.log('DB cleared successfully');
        } catch (e) {
            console.error('Failed to clear the DB storage:', e);
        }
    };


    const chechUpdateUserData = async ()=>{
        
        const reload = await AsyncStorage.getItem("ReloadProfile");
        if(reload === "true"){
            console.log('Aggiornamento... : Modifica del profilo')
            const sid = await AsyncStorage.getItem("SID")
            const uid = await AsyncStorage.getItem("UID")
            let serverData = await CommunicationController.getUserDataByUID(sid, uid)
            setUserData(serverData)   
            await AsyncStorage.setItem("ReloadProfile", "false");
        }else{
            console.log('Aggiornamento non necessario: nessuna interazione')
        }
    }

    const loadUserData = async ()=> {
        const sid = await AsyncStorage.getItem("SID")
        const uid = await AsyncStorage.getItem("UID")
        
        console.log('loadUserData called!')
        //DB - non sappiamo ancora se fa il retireve, db vuoto
        //let dbData = await getUserByUid(db,10)
        //console.log(dbData)
        
        //SERVER
        let serverData = await CommunicationController.getUserDataByUID(sid, uid)
        setUserData(serverData)
        
        setUriImageBase64('data:image/jpeg;base64,'+ serverData.picture)
        setNameTextInput(serverData.name)
        setPosiziones(serverData.positionshare)
    
    }


    //------------------------------Image picker e base64-----------------------------------------
    const imagePicker = async () =>{
        let result = await ImagePicker.launchImageLibraryAsync({
            mediaTypes: ImagePicker.MediaTypeOptions.Images,
            allowsEditing: true,
            aspect: [4, 4],
            quality: 1,
        });
        console.log(result);
        if (!result.canceled) {
            //setImage(result.assets[0].uri);
            convertImageToBase64( result.assets[0].uri)
                .then(base64 => {
                    // Ora hai l'immagine in base64
                    if(getImageSizeFromBase64(base64) >=100){
                        Alert.alert('Errore', 'Immagine supera i 100kb!');
                    }else{
                        setUriImageBase64('data:image/jpeg;base64,'+ base64)
                        console.log('popipopi, immagine inserita')
                        updateProfile(base64)
                    }

                    //console.log('data:image/jpeg;base64,',base64)
                })
                .catch(error => console.error(error));
        }
    };
    const convertImageToBase64 = async (imageUri) => {
        try {
            const manipResult = await ImageManipulator.manipulateAsync(
                imageUri,                
                [],                       
                { base64: true }          
            );

            //console.log('Immagine in base64:', manipResult.base64);
            return manipResult.base64;
        } catch (error) {
            console.error("ERRORE nella conversione dell'immagine:", error);
        }
    };
    const getImageSizeFromBase64 =  (base64String) => {
        
        // Calcola la dimensione in byte
        let sizeInBytes = 4 * Math.ceil((base64String.length / 3)) * 0.5624896334383812;

        // Converti in kilobyte
        let sizeInKB = sizeInBytes / 1024;

        console.log(`Size: ${sizeInKB.toFixed(2)} KB`);
        return sizeInKB;
    }
    const updateProfile = async (base64) =>{
        const sid = await AsyncStorage.getItem("SID")
        const uid = await AsyncStorage.getItem("UID")
        CommunicationController.updateOnlineProfilePic(sid, uid, base64)
    }
    //--------------------------------------------------------------------------------------------


    //------------------------------Funzioni Cambia Nome-----------------------------------------
    const handleOk = () => {
        console.log('Input alert nome:', nameTextInput);
        updateProfileName(nameTextInput)
        setModalVisible(false);
    };
    const updateProfileName = async (newName) => {
        const sid = await AsyncStorage.getItem("SID")
        const uid = await AsyncStorage.getItem("UID")
        CommunicationController.updateOnlineProfileName(sid, uid, newName)
    }
    //-------------------------------------------------------------------------------------------

    //----------------------------------Funzioni cambia positionshare----------------------------
    const changePositionShare = async () => {
        const sid = await AsyncStorage.getItem("SID")
        const uid = await AsyncStorage.getItem("UID")
        CommunicationController.updateOnlinePositionshare(sid, uid, !posiziones)
        showAlertForPosition(!posiziones);
        setPosiziones(!posiziones)
    }
    const showAlertForPosition = (value) => {
        if (value){
            Alert.alert('Modifica Condivisione posizione!', 'Hai impostato la tua posizione visibile a tutti i giocatori');
        }else{
            Alert.alert('Modifica Condivisione posizione!', 'Hai disattivato la condivisione della posizione, nessuno ti vedrà più sulla mappa ');
        }
    };
    //--------------------------------------------------------------------------------------------


    const valAlloggetto =  (id) => {
        if(id ===null){
            console.log('hai cliccato su una casella vuota, l\'id selezionato è null')
            showAlertNoItem()
        }else{
            navigation.navigate('SeeVirtualObj', id)
        }
    }
    const showAlertNoItem = () => {
        Alert.alert('Slot vuoto!', 'Il giocatore non ha equipaggiato nulla in questo slot!');

    };

    const getPlaceholder = (type, idItem) => {
        if (idItem === null) {

            return require('../customIcon/noItem.png');
        } else {
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
                <View style={{ height: 200, width: 200, }}><Image source={{ uri: uriImageBase64 }} style={{ height: 200, width: 200, backgroundColor: '#ccc', resizeMode: 'Image.resizeMode.contain', borderWidth: 0 }} /></View>
                <TouchableOpacity style={styles.tastoimmagine} onPress={() => imagePicker()}><Text>Cambia immagine</Text></TouchableOpacity>
                
            </View>
            <View style={styles.infoBlock}><Text>{nameTextInput} - {userData.uid}</Text>
                <View style={{flexDirection:'row'}}>
                    <Text style={{padding:5}}>HP: {userData.life}</Text>
                    <Text style={{ padding: 5 }}>EXP: {userData.experience}</Text>
                </View>
                <TouchableOpacity onPress={() => setModalVisible(true)}><Text>clicca qui per cambiare nome</Text></TouchableOpacity>
                <TouchableOpacity style={styles.tastoimmagine} onPress={() => navigation.navigate('RankedPlayers') } title="Ranekd"><Text>TOP Ranked Players</Text></TouchableOpacity>
            </View>
            <View style={styles.itemsBlock}>
                

            
            
                <View style={styles.item}><TouchableOpacity style={styles.tastooggettini} onPress={() => valAlloggetto(userData.weapon)} title="Ranekd"><Text>Visualizza</Text></TouchableOpacity>
                    <View style={{ height: 120, width: 120, justifyContent: 'center', alignItems: 'center' }}>
                        <Image source={getPlaceholder('weapon', userData.weapon)} style={{ height: 100, width: 100, resizeMode: 'Image.resizeMode.contain', borderWidth: 0 }} />
                    </View>
                </View>
                <View style={styles.item}><TouchableOpacity style={styles.tastooggettini} onPress={() => valAlloggetto(userData.armor)} title="Ranekd"><Text>Visualizza</Text></TouchableOpacity>
                    <View style={{ height: 120, width: 120, justifyContent: 'center', alignItems: 'center' }}>
                        <Image source={getPlaceholder('armor', userData.armor)} style={{ height: 100, width: 100, resizeMode: 'Image.resizeMode.contain', borderWidth: 0 }} />
                    </View>
                </View>
                <View style={styles.item}><TouchableOpacity style={styles.tastooggettini} onPress={() => valAlloggetto(userData.amulet)} title="Ranekd"><Text>Visualizza</Text></TouchableOpacity>
                    <View style={{ height: 120, width: 120, justifyContent: 'center', alignItems: 'center' }}>
                        <Image source={getPlaceholder('amulet', userData.amulet)} style={{ height: 100, width: 100, resizeMode: 'Image.resizeMode.contain', borderWidth: 0 }} />
                    </View>
                </View>
            
            
            
            
            </View>
            <View style={styles.buttonsBlock}>
                
                <TouchableOpacity style={styles.tastoimmagine} onPress={() => changePositionShare()}><Text>Condividi posizione</Text></TouchableOpacity>
                <TouchableOpacity onPress={() => clearAsyncStorage() }><Text>Testing: elimina tutti i dati del ASYNC della app</Text></TouchableOpacity>
                <TouchableOpacity onPress={() => clearDBStorage()}><Text>Testing: elimina tutti i dati DB della app</Text></TouchableOpacity>
            </View>

            {/*FINESTRA DI DIALOGO PER CAMBIARE NOME*/}
            <Modal
                animationType="slide"
                transparent={true}
                visible={modalVisible}
                onRequestClose={() => setModalVisible(false)}
            >
                <KeyboardAvoidingView
                    behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
                    style={styles.centeredView}
                >
                    <View style={styles.modalView}>
                        <Text style={styles.modalText}>Inserisci un valore</Text>
                        <TextInput
                            style={styles.input}
                            placeholder="Inserisci testo"
                            value={nameTextInput}
                            onChangeText={setNameTextInput}
                        />
                        <View style={styles.buttonContainer}>
                            <Button title="Annulla" onPress={() => setModalVisible(false)} />
                            <Button title="OK" onPress={handleOk} />
                        </View>
                    </View>
                </KeyboardAvoidingView>
            </Modal>

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
    imgBlock:{
        flex: 3,
        //backgroundColor: 'green',
        width: '100%',
        alignItems: 'center',
        justifyContent: 'center',
        padding:10
    },
    infoBlock:{
        flex: 1.5,
        //backgroundColor: 'blue',
        alignItems: 'center',
        justifyContent: 'center',
        width: '100%'
    },
    itemsBlock:{
        flex: 2.5,
        //backgroundColor: 'orange',
        alignItems: 'center',
        justifyContent: 'center',
        width: '100%',
        flexDirection:'row'
    },
    buttonsBlock:{
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
    tastoimmagine:{
        height:50,
        width:200,
        backgroundColor:'skyblue',
        justifyContent:'center',
        alignItems:'center',
        margin:5
    },
    item:{
        margin:5,
        width: 120,
        height: 160,
        backgroundColor:'gray'
    },
    tastooggettini:{
        height: 40,
        
        backgroundColor: 'skyblue',
        justifyContent: 'center',
        alignItems: 'center',
    },

    //parte per la finestra Modale
    centeredView: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    modalView: {
        margin: 20,
        padding: 35,
        backgroundColor: 'white',
        borderRadius: 10,
        alignItems: 'center',
        shadowColor: '#000',
        shadowOpacity: 0.25,
        shadowRadius: 4,
        elevation: 5,
    },
    modalText: {
        marginBottom: 15,
        textAlign: 'center',
    },
    input: {
        height: 40,
        borderColor: 'gray',
        borderWidth: 1,
        width: 200,
        marginBottom: 15,
        paddingHorizontal: 10,
    },
    buttonContainer: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        width: '100%',
    },
    //fine finestra modale
});