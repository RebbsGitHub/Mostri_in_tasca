import AsyncStorage from '@react-native-async-storage/async-storage';
import CommunicationController from '../CommunicationComponents/CommunicationController'

// cose AsyncStorage

export const checkSID_UID = async () => {
    console.log('Check sid - uid ... ')
    try {
        const SID = await AsyncStorage.getItem("SID")
        const UID = await AsyncStorage.getItem("UID")
        if (SID == null || SID == '' || SID.length < 1 || UID ==null || UID == '') {
            //null = mai inizializzato
            //chiamata per chiedere il SID
            try {
                const data = await CommunicationController.register(); // Effettua la chiamata al server
                console.log('SID chiesto: '+await data.sid)
                console.log('UID chiesto: '+await data.uid)
                await AsyncStorage.setItem("SID", await data.sid);
                await AsyncStorage.setItem("UID", await data.uid.toString());

                return true;

            } catch (error) {
                console.error("Errore nella registrazione (primo Avvio - a):", error);

                return true;
                
            } 
    

            //--
        }else{
            console.log('Abbiamo sid-uid')
            const sidStored = await AsyncStorage.getItem("SID")
            const uidStored = await AsyncStorage.getItem("UID")
            console.log('SID avuto: ' + sidStored)
            console.log('UID avuto: ' + uidStored)

            return true;

        }
        
    } catch (e) {
        console.log(" Errore nella registrazione (primo Avvio - b):", e)

        return true;

    }

};