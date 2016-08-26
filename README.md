# React-Native-DataWedge-Intents
React Native Android module to interface with Zebra's DataWedge Intent API

[![npm version](http://img.shields.io/npm/v/react-native-datawedge-intents.svg?style=flat-square)](https://npmjs.org/package/react-native-datawedge-intents "View this project on npm")
[![npm downloads](http://img.shields.io/npm/dm/react-native-datawedge-intents.svg?style=flat-square)](https://npmjs.org/package/react-native-datawedge-intents "View this project on npm")
[![npm licence](http://img.shields.io/npm/l/react-native-datawedge-intents.svg?style=flat-square)](https://npmjs.org/package/react-native-datawedge-intents "View this project on npm")

This module is useful when developing React Native applications for Zebra mobile computers, making use of the Barcode Scanner

### Installation

```bash
npm install react-native-datawedge-intents --save
react-native link react-native-datawedge-intents 
```
Note: as of ReactNative version 0.27 automatic installation of modules is supported via react-native link ... If you are running a version earlier than 0.26 then you will be required to manually install the module.  More detail on manual installation of a typical module can be found [here](https://github.com/Microsoft/react-native-code-push#plugin-installation-android---manual).

## Example usage

**Please see [RNDataWedgeIntentDemo](https://github.com/darryncampbell/RNDataWedgeIntentDemo) for a sample application that makes use of this module**, file [index.android.js](https://github.com/darryncampbell/RNDataWedgeIntentDemo/blob/master/index.android.js)

```javascript
import DataWedgeIntents from 'react-native-datawedge-intents'
...
//  Register a receiver for the barcode scans with the appropriate action
DataWedgeIntents.registerReceiver('com.zebra.dwintents.ACTION', '');
...
//  Declare a handler for barcode scans
this.scanHandler = (deviceEvent) => {console.log(deviceEvent);};
...
//  Listen for scan events sent from the module
DeviceEventEmitter.addListener('barcode_scan', this.scanHandler);
...
//  Initiate a scan (you could also press the trigger key)
DataWedgeIntents.sendIntent(DataWedgeIntents.ACTION_SOFTSCANTRIGGER,DataWedgeIntents.START_SCANNING);

```

## DataWedge

This module **requires the DataWedge service running** on the target device to be **correctly configured** to broadcast Android intents on each barcode scan with the appropriate action:

![Associate app](https://raw.githubusercontent.com/darryncampbell/react-native-datawedge-intents/master/screens/datawedge.png)

For more information about DataWedge and how to configure it please visit Zebra [tech docs](http://techdocs.zebra.com/).  The DataWedge API that this module calls is detailed [here](http://techdocs.zebra.com/datawedge/5-0/guide/api/)

