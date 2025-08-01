export default class CommunicationController{

    //Base url to server
    static BASE_URL = "https://develop.ewlab.di.unimi.it/mc/mostri/";

    // Base function to make general HTTPS Request
    static async genericRequest(endpoint, verb, queryParams, bodyParams) {
        const queryParamsFormatted = new URLSearchParams(queryParams).toString();
        const url = this.BASE_URL + endpoint + "?" + queryParamsFormatted;
        console.log("sending " + verb + " request to: " + url);
        let fatchData = {
            method: verb,
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json'
            }
        };
        if (verb != 'GET') {
            fatchData.body = JSON.stringify(bodyParams);
        }
        httpResponse = await fetch(url, fatchData);

        const status = httpResponse.status;
        if (status == 200) {
            console.log("CODE: 200")
            console.log('httpRes: ' + httpResponse);
            let deserializedObject = await httpResponse.json();
            return deserializedObject;
        } else {
            console.log('CODE: ' + status)

            const message = await httpResponse.text();
            let error = new Error("Error message from the server. HTTP status: " + status + " " + message);
            throw error;
        }
    }

    //-------------------------------------------
    //          Funzioni da chiamare
    //-------------------------------------------

    static async register() {
        const endPoint = "users/";
        const verb = 'POST';
        const queryParams = {};
        const bodyParams = {};
        return await CommunicationController.genericRequest(endPoint, verb, queryParams, bodyParams);
    }


    static async getRanking(sid) {
        const endPoint = "ranking/";
        const verb = 'GET';
        const queryParams = { sid: sid};
        const bodyParams = {};
        return await CommunicationController.genericRequest(endPoint, verb, queryParams, bodyParams);
    }


    static async getUserDataByUID(sid,uid) {
        let endPoint = "users/" + uid+"/";
        const verb = 'GET';
        const queryParams = { sid: sid };
        const bodyParams = {};
        return await CommunicationController.genericRequest(endPoint, verb, queryParams, bodyParams);
    }

    
    static async updateOnlineProfilePic(sid, uid,base64) {
        let endPoint = "users/" + uid + "/";
        const verb = 'PATCH';
        const queryParams = {};
        const bodyParams = { sid: sid , picture: base64};
        return await CommunicationController.genericRequest(endPoint, verb, queryParams, bodyParams);
    }
    

    static async updateOnlineProfileName(sid, uid, newName) {
        let endPoint = "users/" + uid + "/";
        const verb = 'PATCH';
        const queryParams = {};
        const bodyParams = { sid: sid, name: newName };
        return await CommunicationController.genericRequest(endPoint, verb, queryParams, bodyParams);
    }


    static async updateOnlinePositionshare(sid, uid, newPositionshare) {
        let endPoint = "users/" + uid + "/";
        const verb = 'PATCH';
        const queryParams = {};
        const bodyParams = { sid: sid, positionshare: newPositionshare };
        return await CommunicationController.genericRequest(endPoint, verb, queryParams, bodyParams);
    }


    static async getRankedlist(sid) {
        let endPoint = "ranking/";
        const verb = 'GET';
        const queryParams = { sid: sid };
        const bodyParams = {};
        return await CommunicationController.genericRequest(endPoint, verb, queryParams, bodyParams);
    }


    static async getItemById(sid,idItem) {
        let endPoint = "objects/" + idItem + "/";
        const verb = 'GET';
        const queryParams = { sid: sid };
        const bodyParams = {};
        return await CommunicationController.genericRequest(endPoint, verb, queryParams, bodyParams);
    }


    static async getItemsAround(sid, lat, lon) {
        let endPoint = "objects/";
        const verb = 'GET';
        const queryParams = { sid: sid, lat: lat, lon: lon };
        const bodyParams = {};
        return await CommunicationController.genericRequest(endPoint, verb, queryParams, bodyParams);
    }


    static async activateItem(sid, itemID){
        let endPoint = "objects/" + itemID + "/activate";
        const verb = 'POST';
        const queryParams = {};
        const bodyParams = { sid: sid };
        return await CommunicationController.genericRequest(endPoint, verb, queryParams, bodyParams);
    }
    

    static async getPlayersAround(sid, lat, lon) {
        let endPoint = "users/";
        const verb = 'GET';
        const queryParams = { sid: sid, lat: lat, lon: lon };
        const bodyParams = {};
        return await CommunicationController.genericRequest(endPoint, verb, queryParams, bodyParams);
    }

}// fine classe CommunicationController