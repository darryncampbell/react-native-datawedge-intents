/**
 * @providesModule DataWedgeIntents
 */

'use strict';

const { Platform, NativeModules } = require('react-native');
const RNDataWedgeIntents = NativeModules.DataWedgeIntents;

let DataWedgeIntents = {}

try {
    DataWedgeIntents = {
        //  Specifying the DataWedge API constants in this module is deprecated.  It is not feasible to stay current with the DW API.
        ACTION_SOFTSCANTRIGGER: RNDataWedgeIntents.ACTION_SOFTSCANTRIGGER,
        ACTION_SCANNERINPUTPLUGIN: RNDataWedgeIntents.ACTION_SCANNERINPUTPLUGIN,
        ACTION_ENUMERATESCANNERS: RNDataWedgeIntents.ACTION_ENUMERATESCANNERS,
        ACTION_SETDEFAULTPROFILE: RNDataWedgeIntents.ACTION_SETDEFAULTPROFILE,
        ACTION_RESETDEFAULTPROFILE: RNDataWedgeIntents.ACTION_RESETDEFAULTPROFILE,
        ACTION_SWITCHTOPROFILE: RNDataWedgeIntents.ACTION_SWITCHTOPROFILE,
        START_SCANNING: RNDataWedgeIntents.START_SCANNING,
        STOP_SCANNING: RNDataWedgeIntents.STOP_SCANNING,
        TOGGLE_SCANNING: RNDataWedgeIntents.TOGGLE_SCANNING,
        ENABLE_PLUGIN: RNDataWedgeIntents.ENABLE_PLUGIN,
        DISABLE_PLUGIN: RNDataWedgeIntents.DISABLE_PLUGIN,

        sendIntent(action, parameterValue) {
            //  THIS METHOD IS DEPRECATED, use SendBroadcastWithExtras
            RNDataWedgeIntents.sendIntent(action, parameterValue);
        },
        sendBroadcastWithExtras(extrasObject) {
            RNDataWedgeIntents.sendBroadcastWithExtras(extrasObject);
        },
        registerBroadcastReceiver(filter) {
            RNDataWedgeIntents.registerBroadcastReceiver(filter);
        },
        registerReceiver(action, category) {
            //  THIS METHOD IS DEPRECATED, use registerBroadcastReceiver
            RNDataWedgeIntents.registerReceiver(action, category);
        },
    };
} catch (error) {
    console.warn("DataWedgeIntents is not available");
}



module.exports = DataWedgeIntents;
