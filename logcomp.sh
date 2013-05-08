#!/bin/sh
echo "注意修改配置文件conf/?.xml"
jopts="-Xms1024m -Xmx1024m"
jcps="-cp dist/*:lib/*:lib/plugins/estimators/*"
ds=19
trace=64
echo $ds,$trace
ipath="./data"
opath="./result"
x="B"
java $jopts $jcps howmuch.LogCompleteness "$ipath/*.mxml*" -l 0.9 -c conf/lc.xml |grep "mxml,L">$opath/lc$x.res
java $jopts $jcps howmuch.ParseEstimationResult $opath/lc$x.res -u $trace -t $trace -c conf/lc.xml



