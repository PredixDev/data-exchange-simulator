<a href="http://predixdev.github.io/data-exchange-simulator/javadocs/index.html" target="_blank" >
	<img height="50px" width="100px" src="images/javadoc.png" alt="view javadoc"></a>
&nbsp;
<a href="http://predixdev.github.io/data-exchange-simulator" target="_blank">
	<img height="50px" width="100px" src="images/pages.jpg" alt="view github pages">
</a>

## Data Simulator

The data simulator is a simple Java application you can use to push data into your time series database.  The installation script below will perform these basic steps for you:

1. Download the compiled Java application.
2. Configure the application for your UAA and Time Series zone.
3. Push the application to the Predix Cloud.
4. Start a simulation to generate data for the Compressor-CMMS-Compressor-2018 asset.

## Install the Data Simulator

Note: the Simulator works for a stand alone Predix Time Series or APM or any existing Predix Time Series instance. 

### Stand alone Predix Time Series

Please login to your cloud account with the Predix CLI before running the script.

The script assumes you installed a UAA and Time Series in your Cloud org/space.  If you haven't created one yet you can do the following:

change **your-name-uaa** to something unique.
```sh
px create-service predix-uaa Free your-name-uaa
```

change **your-name-time-series** to something unique.
```sh
px cs predix-timeseries Free your-name-time-series your-name-uaa --client-id app_client_id
```

Now you can run the script for your stand alone time series

The following will be asked for in the script: 
```sh
bash <( curl https://raw.githubusercontent.com/PredixDev/data-exchange-simulator/master/scripts/quickstart-dx-simulator.sh )
```
The script will prompt you for some information about:
 - UAA instance issuerId URL - From the APM email, take the UAA URL and add /oauth/token at the end
OR in APM UI go to the Menu option: Admin/Setup then copy the Token Request URL
- Time Series Zone ID - look in the APM email
- Time Series Client ID - look in the APM email
- Time Series Client Secret - look in the APM email

### APM 

Be sure to focus on the APM suggestions in the script and ignore suggestions about stand alone UAA or Time Series.

The script will prompt you for some information about your APM tenant:  All this information can be found in an email you or your APM Tenant Admin received when your APM tenant was created.

 - UAA instance issuerId URL - From the APM email, take the UAA URL and add /oauth/token at the end
OR in APM UI go to the Menu option: Admin/Setup then copy the Token Request URL
- Time Series Zone ID - look in the APM email
- Time Series Client ID - look in the APM email
- Time Series Client Secret - look in the APM email


On DevBox, open a Terminal window and run the following command.

```sh
bash <( curl https://raw.githubusercontent.com/PredixDev/data-exchange-simulator/master/scripts/quickstart-dx-simulator.sh ) --skip-setup
```

On Mac OSX, open a Terminal window and run the following command.

```sh
bash <( curl https://raw.githubusercontent.com/PredixDev/data-exchange-simulator/master/scripts/quickstart-dx-simulator.sh )
```

On Windows, open a Command window and run the following command.
```bat
@powershell -Command "(new-object net.webclient).DownloadFile('https://raw.githubusercontent.com/PredixDev/data-exchange-simulator/master/scripts/quickstart-dx-simulator.bat','quickstart-dx-simulator.bat')" && "quickstart-dx-simulator.bat"
``` 

## Use the Data Simulator

To see all the details of the simulation, you can open the simulation file that was downloaded by the script: 

https://raw.githubusercontent.com/PredixDev/data-exchange-simulator/develop/scripts/sample-simulation.json

The simulator json API has the following characteristics:

- The assetId and nodeName are concatenated (by default, separated by a period) to form the Time Series tag.  e.g. Compressor-CMMS-Compressor-2018.crank-frame-suctionpressure
- start time of 0 means "now".  start time can be set to "unix epoch" time, which is the number of milliseconds since Jan 1, midnight, 1970
- interval is the time between each data point
- An array of Ranges define the behavior of the simulated data.  
- For each range a RANDOM number is generated between the lowerThreshold and upperThreshold.  In the example below a spike will be generated with values between 3 and 4
- When the simulator reaches the end of the Ranges array, it goes back to the beginning.

```json
{
  "name": "Sample-Random-Simulation-1",
  "tagSet": {
      "tag": [
        {
          "assetId": "Compressor-CMMS-Compressor-2018",
          "nodeName": "crank-frame-suctionpressure",
          "start": 0,
          "interval": 3,
          "dataType": "DOUBLE",
          "simulationType": "RANDOM",
          "range": [
            {
              "lowerThreshold": 2.5,
              "upperThreshold": 3.0,
              "duration": 21
            }, 
            {
              "lowerThreshold": 3.0,
              "upperThreshold": 4.0,
              "duration": 9
            },
            {
              "lowerThreshold": 2.5,
              "upperThreshold": 3.0,
              "duration": 21
            }
          ]
        }
      ]
  }
}
```

You could modify this file to simulate data for any time series tag with any shape of data.

The Simulator runs what you give it.  
To start the simulation, call the start-simulation url, providing the path to the JSON file.
```sh
curl https://MY-NAME-data-exchange-simulator.run.aws-usw02-pr.ice.predix.io/start-simulation -X POST -H "Content-Type: application/json" --data-binary "@/full/path-to/dx-simulator/predix-scripts/data-exchange-simulator/scripts/sample-simulation.json"' 
```

The simulator will continue running indefinitely. To stop the simulation, run this command. Be sure to use the correct endpoint for your simulator.

```sh
curl -X POST https://MY-NAME-data-exchange-simulator.run.aws-usw02-pr.ice.predix.io/stop-simulation -H 'cache-control: no-cache' -H 'Content-Type: text/plain' --data Sample-Random-Simulation-1
```

To get the list of simulations run this
```sh
curl -X GET http://localhost:8080/simulations -H 'cache-control: no-cache' -H 'Content-Type: text/plain'
```



[![Analytics](https://ga-beacon.appspot.com/UA-82773213-1/data-exchange-simulator/readme?pixel)](https://github.com/PredixDev)
