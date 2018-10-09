#!/bin/bash
# these variables are avaliable on Jenkins{githubUrl}/${repo}/${branchName}
curl -O ${githubUrl}/${repo}/${branchName}/scripts/dx-simulator.expect;
chmod 755 dx-simulator.expect
#expect dx-simulator.expect ${githubUrl}/${repo}/${branchName}/scripts --skip-setup  uswestsmoketestedisonbas https://4f6d1d4b-c6a3-48e9-8963-18656ae191f0.predix-uaa.run.aws-usw02-pr.ice.predix.io/oauth/token 51bd1e37-0872-48dd-86a6-5644cb0877e2

expect dx-simulator.expect ${githubUrl}/${repo}/${branchName}/scripts ${moreSwitches} ${instancePrefix} ${UAA_ISSUREID} ${TS_ZONE}