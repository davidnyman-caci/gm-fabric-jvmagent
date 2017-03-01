#!/bin/bash -

{

COMMAND=`which aglio`
if [ "${COMMAND}" == "" ]
then
  echo "Unable to generate documentation. No conversion tool found."
  exit -1
fi

SOURCE="${1}"
DESTINATION="${2}"
FILE="${3}"

if [ "${SOURCE}" == "" ] || [ "${DESTINATION}" == "" ] || [ "${FILE}" == "" ]
then
  echo "Cannot generate documention. One or more attributes are empty. See below:"
  echo "SOURCE      = [ ${SOURCE} ]"
  echo "DESTINATION = [ ${DESTINATION} ]"
  echo "FILE        = [ ${FILE} ]"
  exit -1
fi


if [ ! -e "${DESTINATION}" ]
then
   mkdir -p "${DESTINATION}"
fi

${COMMAND} -i "${SOURCE}/${FILE}.md" -o "${DESTINATION}/${FILE}.html"

}
