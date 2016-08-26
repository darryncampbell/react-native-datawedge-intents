/**
 * @providesModule DataWedgeIntents
 */

'use strict';

var { Platform, NativeModules } = require('react-native');
var RNDataWedgeIntents = NativeModules.DataWedgeIntents;

var DataWedgeIntents = {
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
        RNDataWedgeIntents.sendIntent(action, parameterValue);
    },
    registerReceiver(action, category) {
        RNDataWedgeIntents.registerReceiver(action, category);
    },
};

module.exports = DataWedgeIntents;
