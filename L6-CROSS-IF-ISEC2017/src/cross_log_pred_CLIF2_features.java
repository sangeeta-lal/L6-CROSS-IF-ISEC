import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.RBFNetwork;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.Stacking;
import weka.classifiers.meta.Vote;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.trees.ADTree;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.Standardize;
import weka.filters.unsupervised.attribute.StringToWordVector;

// This file will be used to ensemble based prediction using stacking of algorithms
public class cross_log_pred_CLIF2_features
{



/*
String path = "E:\\Sangeeta\\Research\\";
String user_name =  "sangeetal";
String password = "sangeetal";
String url = "jdbc:mysql://localhost:3307/";
String driver = "com.mysql.jdbc.Driver"; 
 
// */

///*
String path = "F:\\Research\\";
String user_name =  "root";
String password = "1234";
String url = "jdbc:mysql://localhost:3306/";
String driver = "com.mysql.jdbc.Driver";
//*/


//String type = "catch";
String type = "if";

int iterations=1;
String source_project="tomcat";
String target_project = "cloudstack";
//String target_project="hd";

//String source_project="cloudstack";
//String target_project = "tomcat";
//String target_project="hd";

//String source_project="hd";
//String target_project = "tomcat";
//String target_project="cloudstack";

String db_name ="logging6_isec";
String result_table = "cross_pred_bool_feature_"+type;

String source_num_file_path = path+"L6-CROSS-IF-ISEC2017\\dataset\\"+source_project+"-arff\\"+type+"\\num-features\\"+source_project+"_"+type+"_num_features.arff";		
String target_num_file_path = path+"L6-CROSS-IF-ISEC2017\\dataset\\"+target_project+"-arff\\"+type+"\\num-features\\"+target_project+"_"+type+"_num_features.arff";

String source_bool_file_path = path+"L6-CROSS-IF-ISEC2017\\dataset\\"+source_project+"-arff\\"+type+"\\bool-features\\"+source_project+"_"+type+"_bool_features.arff";		
String target_bool_file_path = path+"L6-CROSS-IF-ISEC2017\\dataset\\"+target_project+"-arff\\"+type+"\\bool-features\\"+target_project+"_"+type+"_bool_features.arff";


DataSource num_trainsource;
DataSource bool_trainsource;

DataSource num_testsource;
DataSource bool_testsource;

Instances num_trains;
Instances bool_trains;

Instances num_tests;
Instances bool_tests;

Evaluation result;

int instance_count_source = 0;
int instance_count_target =0;
Connection conn=null;	
java.sql.Statement stmt = null;


//This function uses dataset from the ARFF files
public void read_file()
{ 
try 
	{
	
		num_trainsource = new DataSource(source_num_file_path);
		num_trains = num_trainsource.getDataSet();
		num_trains.setClassIndex(0);
		
		num_testsource = new DataSource(target_num_file_path);
		num_tests = num_testsource.getDataSet();		
		num_tests.setClassIndex(0);
		
		
		bool_trainsource = new DataSource(source_bool_file_path);
		bool_trains = bool_trainsource.getDataSet();
		bool_trains.setClassIndex(0);
		
		bool_testsource = new DataSource(target_bool_file_path);
		bool_tests = bool_testsource.getDataSet();		
		bool_tests.setClassIndex(0);
		
		
		instance_count_source = num_trains.numInstances();
		instance_count_target = num_tests.numInstances();
		
		//System.out.println("Instance count source ="+ instance_count_source + "  Instance count target="+ instance_count_target);
   
	} catch (Exception e) 
	{
	
		e.printStackTrace();
	}	  
	
}


//This function is used to pre-process the dataset
public void pre_process_num_data()
{

 try
   {
	 
	 // dont do anything for numerical features
	  /*
	  //1. TF-IDF
	  StringToWordVector tfidf_filter = new StringToWordVector();
	  tfidf_filter.setIDFTransform(true);
	  tfidf_filter.setInputFormat(trains);
	  trains = Filter.useFilter(trains, tfidf_filter);     	  
	
	  tests = Filter.useFilter(tests, tfidf_filter);
 
     

     //2. Standarize  (not normalize because normalization is affected by outliers very easily)   	  
	  Standardize  std_filter =  new Standardize();
	  std_filter.setInputFormat(trains);
	  trains= Filter.useFilter(trains,std_filter);     	  
	 
	  tests= Filter.useFilter(tests,std_filter);  	
     

     //3. Discretizations
	  Discretize dfilter = new Discretize();
     dfilter.setInputFormat(trains);
     trains = Filter.useFilter(trains, dfilter);
     
     tests = Filter.useFilter(tests, dfilter);	*/ 
     


	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

}



//This function is used to train and test a using a given classifier
public Evaluation cross_pred_random_forest_num_bool(float a, float b) 
{
	
	
Evaluation evaluation = null;
RandomForest  num_m1 =  new RandomForest();
RandomForest  bool_m1 =  new RandomForest();


try
{

		
    m1.buildClassifier(trains);
	evaluation= new Evaluation(trains);
	evaluation.evaluateModel(m1, tests);


} catch (Exception e) 
{

	e.printStackTrace();
}

return evaluation;

}
	



public Connection initdb(String db_name)
{
try {
	      Class.forName(driver).newInstance();
	      conn = DriverManager.getConnection(url+db_name,user_name,password);
	      //System.out.println(" dbname="+ db_name+ "user name"+ userName+ " password="+ password);
	      if(conn==null)
	      {
	    	  System.out.println(" Database connection is null. Check it.");
	      }
	      
	 } catch (Exception e) 
	 {
	      e.printStackTrace();
	 }
	return conn;
}


//This method computes the average value  and std. deviation and inserts them in a db
public void compute_avg_stdev_and_insert(String classifier_name, double[] precision, double[] recall, double[] accuracy, double[] fmeasure, double[] roc_auc) 
{

// computes following metrics:
	/*
	 * 1. Precision
	 * 2. Recall
	 * 3. Accuracy
	 * 4. F measure
	 * 5. ROC-AUC
	 * */

	double avg_precision = 0.0;
	double avg_recall = 0.0;
	double avg_accuracy = 0.0;
	double avg_fmeasure = 0.0;	
	double avg_roc_auc = 0.0;
	
	double std_precision = 0.0;
	double std_recall = 0.0;
	double std_accuracy = 0.0;
	double std_fmeasure = 0.0;	
	double std_roc_auc = 0.0;
	//double total_instances = 0.0;
	
	util6_met_isec  ut = new util6_met_isec();
	
	avg_precision   = ut.compute_mean(precision);
	avg_recall      = ut.compute_mean(recall);
	avg_fmeasure    = ut.compute_mean(fmeasure);
	avg_accuracy    = ut.compute_mean(accuracy);
	avg_roc_auc     = ut.compute_mean(roc_auc);
	
	std_precision   = ut.compute_stddev(precision);
	std_recall      = ut.compute_stddev(recall);
	std_fmeasure    = ut.compute_stddev(fmeasure);
	std_accuracy    = ut.compute_stddev(accuracy);
	std_roc_auc     = ut.compute_stddev(roc_auc);
	
		
  // System.out.println("model ="+classifier_name +"   Acc = "+ avg_accuracy + "  size="+ pred_10_db.size());
	
	String insert_str =  " insert into "+ result_table +"  values("+ "'"+ source_project+"','"+ target_project+"','"+ classifier_name+"',"+ trains.numInstances() + ","+ tests.numInstances()+","
	                       + iterations+","+trains.numAttributes() +","+avg_precision+","+ std_precision+","+ avg_recall+","+ std_recall+","+avg_fmeasure+","+std_fmeasure+","+ avg_accuracy 
	                       +","+std_accuracy+","+ avg_roc_auc+","+ std_roc_auc+" )";
	System.out.println("Inserting="+ insert_str);
	
	conn = initdb(db_name);
	if(conn==null)
	{
		System.out.println(" Databasse connection is null");
		
	}
	
	try 
	{
		stmt = conn.createStatement();
		stmt.executeUpdate(insert_str);
		stmt.close();
		conn.close();
	} catch (SQLException e) {
		
		e.printStackTrace();
	}

}



private void learn_and_insert_random_forest(double[] precision,
		double[] recall, double[] accuracy, double[] fmeasure, double[] roc_auc) 
{
System.out.println("Computing  Random Forest for:"+ type);  
	
	//\\=========== Decision table=================================//\\			
		
      for(double a1=0; a1<=1.0; a1= a1+0.1)   
      {
    	  for(double b1=0; b1<=1.0;b1=b1+0.1)
    	  {
    		  for(int i=0; i<iterations; i++)
    		  {
			
			    read_file();
			   
				//pre_process_num_data();  //@Not needed
			    //pre_process_bool_data(); //@Not needed
			    
				result = cross_pred_random_forest(a1, b1);				
				
				precision[i]         =   result.precision(1)*100;
				recall[i]            =   result.recall(1)*100;
				accuracy[i]          =   result.pctCorrect(); //not required to multiply by 100, it is already in percentage
				fmeasure[i]          =   result.fMeasure(1)*100;
				roc_auc[i]           =   result.areaUnderROC(1)*100;		
			
				//@ Un comment to see the evalauation results
				//System.out.println(clp.result.toSummaryString());			
					
			}
				  
		   compute_avg_stdev_and_insert("Random Forest", precision, recall, accuracy, fmeasure , roc_auc );	   

    	  }//b1
      }//a1   
   }


//This is the main function
public static void main(String args[])
{	  	

	  cross_log_pred_CLIF2_features clps =  new cross_log_pred_CLIF2_features();
	
	  double precision[]   = new double[clps.iterations];
	  double recall[]      = new double[clps.iterations];
	  double accuracy[]    = new double[clps.iterations];
	  double fmeasure[]    = new double[clps.iterations];	
	  double roc_auc[]     = new double[clps.iterations];
		
	 // clps.learn_and_insert_adtree(precision, recall, accuracy,fmeasure,roc_auc);
	  //clps.learn_and_insert_decision_table(precision, recall, accuracy,fmeasure,roc_auc);
	  //clps.learn_and_insert_j48(precision, recall, accuracy,fmeasure,roc_auc);
	  //clps.learn_and_insert_logistic(precision, recall, accuracy,fmeasure,roc_auc);
	  clps.learn_and_insert_random_forest(precision, recall, accuracy,fmeasure,roc_auc);
	  //clps.learn_and_insert_naive_bayes(precision, recall, accuracy,fmeasure,roc_auc);
	  //clps.learn_and_insert_bayes_net(precision, recall, accuracy,fmeasure,roc_auc);
	  //clps.learn_and_insert_adaboost(precision, recall, accuracy,fmeasure,roc_auc);
	  //clps.learn_and_insert_rbfnetwork(precision, recall, accuracy,fmeasure,roc_auc);
    
     }//main	
	
}// classs




