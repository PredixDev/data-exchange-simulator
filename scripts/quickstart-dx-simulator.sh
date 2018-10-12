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
     -cci|--custom-client-id)
      QUICKSTART_ARGS+=" $1 $2"
      shift
    ;;
    -ccs|--custom-client-secret)
      QUICKSTART_ARGS+=" $1 $2"
      shift
     ;;
    -tiu|--trusted-issuer-url)
      QUICKSTART_ARGS+=" $1 $2"
      shift
    ;;
     -ctsz|--custom-timeseries-zone)
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
	      *)
      QUICKSTART_ARGS+=" $1"
      echo $1
    ;;
  esac
  shift
  done

  if [[ -z $BRANCH ]]; then
    echo "Usage: $0 -b/--branch <branch> [--skip-setup]"
    exit 1
  fi
}

# default settings
BRANCH="master"
PRINT_USAGE=0
SKIP_SETUP=false
SKIP_PULL=false

IZON_SH="https://raw.githubusercontent.com/PredixDev/izon/1.1.0/izon2.sh"
SCRIPT="-script build-basic-app.sh -script-readargs build-basic-app-readargs.sh"
SIMULATION_FILE="data-exchange-simulator/scripts/sample-simulation.json"
#QUICKSTART_ARGS="-pxclimin 0.6.18 -tiu required -cci required -ccs required -ctsz required -sim -sim-file $SIMULATION_FILE $SCRIPT"
QUICKSTART_ARGS="-pxclimin 0.6.18 -sim -sim-file $SIMULATION_FILE $SCRIPT"
VERSION_JSON="version.json"
PREDIX_SCRIPTS=predix-scripts
REPO_NAME=data-exchange-simulator
APP_DIR="dx-simulator"
APP_NAME="Data Exchange Simulator"
GITHUB_RAW="https://raw.githubusercontent.com/PredixDev"
SCRIPT_NAME=quickstart-dx-simulator.sh
TOOLS="Cloud Foundry CLI, Git, Predix CLI"
TOOLS_SWITCHES="--cf --git --predixcli"


# Process switches
local_read_args $@


if [[ $QUICKSTART_ARGS != *"-ctsz"* ]]; then
 
  QUICKSTART_ARGS+=" -ctsz required "
fi
if [[ $QUICKSTART_ARGS != *"-tiu"* ]]; then

  QUICKSTART_ARGS+=" -tiu required"
fi
if [[ $QUICKSTART_ARGS != *"-ccs"* ]]; then

  QUICKSTART_ARGS+=" -ccs required "
fi
if [[ $QUICKSTART_ARGS != *"-cci"* ]]; then

  QUICKSTART_ARGS+=" -cci required "
fi

echo $QUICKSTART_ARGS

#variables after processing switches
SCRIPT_LOC="$GITHUB_RAW/$REPO_NAME/$BRANCH/scripts/$SCRIPT_NAME"
VERSION_JSON_URL="$GITHUB_RAW/data-exchange-simulator/$BRANCH/version.json"

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
  
  check_internet

  #get the script that reads version.json
  eval "$(curl -s -L $IZON_SH)"

  #download script and cd
  getUsingCurl $SCRIPT_LOC
  chmod 755 $SCRIPT_NAME;
  if [[ ! $currentDir == *"$REPO_NAME" ]]; then
    mkdir -p $APP_DIR
    cd $APP_DIR
  fi

  getVersionFile
  getLocalSetupFuncs $GITHUB_RAW
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

COLUMNS=$(tput cols)
mkdir -p predix-scripts/log
__append_new_head_log "Data Exchange Simulator script" "-" "predix-scripts/log"
__append_new_line_log "This script will install a Data Simulator in your Predix cloud account's space.  You will need to log in to the cloud and also provide some info in order for the Simulator to send data to your secure Time Series Zone.  It is assumed you have an existing Time Series instance." "predix-scripts/log"
__append_new_line_log "" "predix-scripts/log"
__append_new_line_log "Please look up the info and provide answers to the prompts." "predix-scripts/log"
__append_new_line_log "" "predix-scripts/log"
__append_new_line_log "The simulator works for a standalone Predix Time Series or with APM Time Series.  Enter the appropriate info for your situation." "predix-scripts/log"

source $PREDIX_SCRIPTS/bash/quickstart.sh $QUICKSTART_ARGS

__append_new_line_log "Successfully completed $APP_NAME installation!" "$quickstartLogDir"
