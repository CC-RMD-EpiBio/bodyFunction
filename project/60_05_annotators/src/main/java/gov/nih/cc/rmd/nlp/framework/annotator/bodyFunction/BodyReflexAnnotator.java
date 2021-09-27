// =================================================
/**
 * BodyReflexAnnotator finds body reflex evidence in text
 * 
 *  This takes advantage of the term lookup, and adds
 *  concepts based on terms that could have a BodyReflex
 *  category from the lexicon that were found in the text.
 *
 * 
 * @author  G.o.d.
 * @created 2020.05.01
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.Reflex;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Line;





public class BodyReflexAnnotator extends JCasAnnotator_ImplBase {
 
  
  
  // -----------------------------------------
  /**
   * process runs through the text and creates
   * BodyReflexs from mentions.
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
    this.performanceMeter.startCounter();

    List<Annotation> terms = UIMAUtil.getAnnotations(pJCas, LexicalElement.typeIndexID );
    
    if ( terms != null && !terms.isEmpty())
      for ( Annotation term: terms ) {
        
        String categories = ((LexicalElement) term ).getSemanticTypes();
        if ( categories != null && categories.contains("Reflex"))
          if (// !((LexicalElement) term ).getConditional() && 
              !((LexicalElement) term ).getSubject().contains("other") &&
              !((LexicalElement) term ).getAttributedToPatient()   &&
              !((LexicalElement) term ).getHistorical()   &&
             
              !BodyFunctionAnnotator.filterBySection( pJCas, ((LexicalElement) term ) ) )
            createBodyReflex(pJCas, term );
      }
    
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   

// =================================================
  /**
   * createBodyReflex 
   * 
   * @param pJCas
   * @param pTerm
   * @return Reflex
  */
  // =================================================
  private final Reflex createBodyReflex(JCas pJCas, Annotation pTerm) {
   
    
    Reflex statement = new Reflex( pJCas);
    statement.setBegin( pTerm.getBegin());
    statement.setEnd(   pTerm.getEnd() );
  
  
    StringArray cuiz = null;
    String cuis = null;
    cuiz = ((LexicalElement) pTerm).getEuis();
    if ( cuiz != null && cuiz.size() > 0 )
      cuis = VUIMAUtil.convertStringArrayToDelimitedString(cuiz.toArray());
    
    String conceptNames =  ((LexicalElement) pTerm).getCitationForm();
    String semanticTypes =  ((LexicalElement) pTerm).getSemanticTypes();
  
    statement.setId  ( this.getClass().getSimpleName() + "_" + annotationCtr);
    statement.setCuis( cuis);
    statement.setCategories(semanticTypes);
    statement.setConceptNames(conceptNames);
   
    statement.setConditionalStatus( ((LexicalElement) pTerm).getConditional() );
    statement.setSubjectStatus( ((LexicalElement) pTerm).getSubject());
    statement.setAssertionStatus(((LexicalElement) pTerm).getNegation_Status() );
    statement.setHistoricalStatus( ((LexicalElement) pTerm).getHistorical() );
    statement.setAttributedToPatient(((LexicalElement) pTerm).getAttributedToPatient() );
    // ((LexicalElement) pTerm).getGeneric();

    
    statement.addToIndexes();
    String sectionName = VUIMAUtil.deriveSectionName(statement);
    statement.setSectionName(sectionName);
    
    statement.setMarked(false);
    
    return statement;
    
  } // end Method createBodyReflex() ----------




//----------------------------------
/**
 * destroy
* 
 **/
// ----------------------------------
public void destroy() {
  this.performanceMeter.writeProfile( this.getClass().getSimpleName());
}

  
  //----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
       
     
      String[] args = null;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  

        initialize(args);
        
      } catch (Exception e ) {
        String msg = "Issue in initializing class " + this.getClass().getName() + " " + e.toString() ;
        GLog.println(GLog.ERROR_LEVEL, msg);     // <------ use your own logging here
        throw new ResourceInitializationException();
      }
      
   
      
  } // end Method initialize() -------
  
  //----------------------------------
  /**
   * initialize initializes the class.  Parameters are passed in via a String
   *                array  with each row containing a --key=value format.
   *                
   *                It is important to adhere to the posix style "--" prefix and
   *                include a "=someValue" to fill in the value to the key. 
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    
      
  } // end Method initialize() -------
 
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  
  
  
  
} // end Class LineAnnotator() ---------------

