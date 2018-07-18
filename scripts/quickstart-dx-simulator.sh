#!/bin/bash
set -e

function local_read_args() {
  while (( "$#" )); do
  opt="$1"
  case $opt in
    -h|-\?|--\?--help)
      PRINT_USAGE=1
      QUICKSTART_ARGS="$SCRIPT $1"
      break
    ;;
    -b|--branch)
      BRANCH="$2"
      QUICKSTART_ARGS+=" $1 $2"
      shift
    ;;
    -o|--override)
      QUICKSTART_ARGS=" $SCRIPT"
    ;;
    --skip-setup)
      SKIP_SETUP=true
    ;;
    --skip-pull)
      SKIP_PULL=true
    ;;
	  -cci|--custom-client-id)
      if [ -n "$2" ]; then
        UAA_CLIENTID_GENERIC=$2
        shift
      else
        printf 'ERROR: "--custom-client-id" requires a non-empty option argument.\n' >&2
        exit 1
      fi
    ;;
	  -ccs|--custom-client-secret)
      if [ -n "$2" ]; then
        UAA_CLIENTID_GENERIC_SECRET=$2
        shift
      else
        printf 'ERROR: "--custom-client-secret" requires a non-empty option argument.\n' >&2
        exit 1
      fi
    ;;        
	  -tiu|--trusted-issuer-url)
      if [ -n "$2" ]; then
        TRUSTED_ISSUER_ID=$2
        shift
      else
        printf 'ERROR: "--trusted-issuer-url" requires a non-empty option argument.\n' >&2
        exit 1
      fi
    ;;
	  -ctsz|--custom-timeseries-zone)
      if [ -n "$2" ]; then
        TIMESERIES_ZONE_ID=$2
        shift
      else
        printf 'ERROR: "--custom-timeseries-zone" requires a non-empty option argument.\n' >&2
        exit 1
      fi
      ;;
    *)
      QUICKSTART_ARGS+=" $1"
      #echo $1
    ;;
  esac
  shift
  done

  if [[ -z $BRANCH ]]; then
    echo "Usage: $0 -b/--branch <branch> [--skip-setup]"
    exit 1
  fi
}

BRANCH="master"
PRINT_USAGE=0
SKIP_SETUP=false
SKIP_PULL=false
SCRIPT="-script build-basic-app.sh -script-readargs build-basic-app-readargs.sh"
SIMULATION_FILE="data-exchange-simulator/scripts/sample-simulation.json"
QUICKSTART_ARGS="-pxclimin 0.6.18 -sim -sim-file $SIMULATION_FILE $SCRIPT"
VERSION_JSON="version.json"
PREDIX_SCRIPTS=predix-scripts
REPO_NAME=data-exchange-simulator
APP_DIR="dx-simulator"
APP_NAME="Data Exchange Simulator"
SCRIPT_NAME=quickstart-dx-simulator.sh
TOOLS="Cloud Foundry CLI, Git, Predix CLI"
TOOLS_SWITCHES="--cf --git --predixcli"

local_read_args $@
IZON_SH="https://raw.githubusercontent.com/PredixDev/izon/$BRANCH/izon.sh"
VERSION_JSON_URL="https://raw.githubusercontent.com/PredixDev/data-exchange-simulator/$BRANCH/version.json"

function check_internet() {
  set +e
  echo ""
  echo "Checking internet connection..."
  curl "http://google.com" > /dev/null 2>&1
  if [ $? -ne 0 ]; then
    echo "Unable to connect to internet, make sure you are connected to a network and check your proxy settings if behind a corporate proxy"
    echo "If you are behind a corporate proxy, set the 'http_proxy' and 'https_proxy' environment variables."
    exit 1
  fi
  echo "OK"
  echo ""
  set -e
}

function init() {
  currentDir=$(pwd)
  if [[ $currentDir == *"scripts" ]]; then
    echo 'Please launch the script from the root dir of the project'
    exit 1
  fi
  if [[ ! $currentDir == *"$REPO_NAME" ]]; then
    mkdir -p $APP_DIR
    cd $APP_DIR
  fi
  check_internet

  #get the script that reads version.json
  eval "$(curl -s -L $IZON_SH)"

  getVersionFile
  getLocalSetupFuncs
}

if [[ $PRINT_USAGE == 1 ]]; then
  init
  __print_out_standard_usage
else
  if $SKIP_SETUP; then
    init
  else
    init
    __standard_mac_initialization
  fi
fi

echo "getPredixScripts"
getPredixScripts
#clone the repo itself if running from oneclick script
getCurrentRepo

# Prompt user for uaa & ts services
if [[ "$TRUSTED_ISSUER_ID" == "" ]]; then
  read -p $'\nEnter the URL of the Timeseries UAA instance with /oauth/token at the end. \nHint: in another terminal window, type "predix service-info my-uaa-name-here" \nOr: check your APM welcome email\nOr: in APM go to Admin/Setup/Token Request URL> ' TRUSTED_ISSUER_ID
# else
#   SIMULATOR_UAA=$TRUSTED_ISSUER_ID
fi
if [[ "$TIMESERIES_ZONE_ID" == "" ]]; then
  read -p $'\nEnter your Predix Time Series Zone ID. \nHint:in another terminal window, type "predix service-info my-timeseries-name-here" look for zone-http-header-value \nOr: check your APM welcome email> ' TIMESERIES_ZONE_ID
# else
#   SIMULATOR_TIME_SERIES=$CUSTOM_TIMESERIES_INSTANCE
fi
if [[ "$UAA_CLIENTID_GENERIC" == "" ]]; then
  read -p $'\nEnter your UAA client ID. (UAA Client must have authorities for your time series zone.) \nHint:  many tutorials use "app_client_id" \nOr: for APM check your APM welcome email> ' UAA_CLIENTID_GENERIC
fi
if [[ "$UAA_CLIENTID_GENERIC_SECRET" == "" ]]; then
  read -p $'\nEnter your UAA client secret. \nHint: many tutorials use "secret" \nOr: for APM check your APM welcome email> ' UAA_CLIENTID_GENERIC_SECRET
fi

export TRUSTED_ISSUER_ID
export TIMESERIES_ZONE_ID
export UAA_CLIENTID_GENERIC
export UAA_CLIENTID_GENERIC_SECRET

# QUICKSTART_ARGS+=" --custom-uaa $SIMULATOR_UAA --custom-timeseries $SIMULATOR_TIME_SERIES"
#echo "quickstart_args=$QUICKSTART_ARGS"

source $PREDIX_SCRIPTS/bash/quickstart.sh $QUICKSTART_ARGS

__append_new_line_log "Successfully completed $APP_NAME installation!" "$quickstartLogDir"
