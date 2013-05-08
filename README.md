CompletenessFramework
=====================

The framework for evaluating the information completeness of event logs.

Process mining is a sub-field of Business Process Manangement, which aims at 
mining process models from event logs that record the executions of real
business processes. Consequently it is necessarily important to know whether or
not the given event log is information complete, i.e. it contains all 
information required to mine the original process model. This is the so-called
log completeness problem of event logs. The most difficult part of the problem
is that the original process model is unknown. 

The problem has been studied already and some approaches have been proposed to 
evaluate the information completeness of a log. See http://bpmcenter.org/wp-content/uploads/reports/2012/BPM-12-04.pdf
for more detail.

The framework here is proposed to evaluate the approaches available for the 
log completeness probelm. Main features of the framework cover
* defining different types of information unit,
* parsing event logs files (only in mxml format till now),
* implementing approaches available in Java,
* calling these approaches and output the evaluation results into 'csv' files.

By means of the framework, researchers and/or developers would focus on the 
proposal of new approaches. A new approach can be very easily added to the 
framework by implementing the interface 'howmuch.estimator.Estimator' and then 
put the jar file into the directory './lib/plugins/estimators/'.

Run the script 'logcomp.sh' to get the evaluation results of the specified 
approaches on some specified logs. Refer to './conf/lc.conf' for mor information
on configuration of the running.

Refer to https://github.com/hunter99/EstimatorDummy for more information about 
developing a new estimator (the implementation of a new approach).
