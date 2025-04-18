/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.netbeans.modules.xml.wizard.impl;

import org.netbeans.modules.xml.wizard.*;
import java.awt.Component;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.*;
import java.io.*;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openide.loaders.TemplateWizard;
import java.util.Vector;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.netbeans.modules.xml.util.Util;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 * This panel gathers data that are necessary for instantiting of XML
 * document conforming to given XML Schema.
 * <p>
 * Data allows to create a document that respect restrictions of current parser
 * implementations (they use schemaLocation hint specifically).
 *
 * @author  Petr Kuzel
 */
public class SchemaPanel extends AbstractPanel implements ActionListener, TableModelListener {
    /** Serial Version UID */
    private static final long serialVersionUID = -7568909683682244030L;    
    private TemplateWizard templateWizard;
    private FileObject primarySchema=null;
    private Vector rows;
    private static final int PRIMARY_COL = 0;
    private static final int SCHEMA_COL = 1;
    private static final int ROOT_COL = 2;
    private static final int PREFIX_COL = 3;
    private SchemaTableModel tableModel;
    private SchemaImportGUI gui;
    private static String startString;
    /** Prefix for the namespace prefix values (e.g. "ns"). */
    private static final String PREFIX = "ns"; // NOI18N

   /** Hashmap to keep track of prefixes */
   //private Set<String> prefixSet = new HashSet<String>();
   
    
    /** Creates new form SchemaPanel */
    public SchemaPanel(TemplateWizard tw) {
        this.templateWizard = tw;
        rows = new Vector();
        tableModel = new SchemaTableModel();
        initComponents();
        initAccessibility();
        initComp();
      
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        locationPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        schemaTable = new javax.swing.JTable();
        locationLabel = new javax.swing.JLabel();
        browseButton = new javax.swing.JButton();
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });
        removeButton = new javax.swing.JButton();

        setName(Util.THIS.getString(SchemaPanel.class, "PROP_schema_panel_name")); // NOI18N

        locationPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        locationPanel.setLayout(new java.awt.BorderLayout());

        schemaTable.setModel(tableModel);
        jScrollPane1.setViewportView(schemaTable);
        schemaTable.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SchemaPanel.class, "LBL_Schema_table")); // NOI18N
        schemaTable.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SchemaPanel.class, "LBL_Schema_table")); // NOI18N

        locationPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        locationLabel.setLabelFor(locationPanel);
        locationLabel.setText(org.openide.util.NbBundle.getMessage(SchemaPanel.class, "LBL_SchemaPanel_Location")); // NOI18N
        locationLabel.setToolTipText(org.openide.util.NbBundle.getMessage(SchemaPanel.class, "TIP_SchemaPanel_Location")); // NOI18N

        browseButton.setText(org.openide.util.NbBundle.getMessage(SchemaPanel.class, "LBL_BrowseButton")); // NOI18N

        removeButton.setText(org.openide.util.NbBundle.getMessage(SchemaPanel.class, "LBL_RemoveButton")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(locationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 582, Short.MAX_VALUE)
                    .addComponent(locationLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(browseButton)
                        .addGap(18, 18, 18)
                        .addComponent(removeButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(locationLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(locationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(browseButton)
                    .addComponent(removeButton))
                .addContainerGap(82, Short.MAX_VALUE))
        );

        browseButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SchemaPanel.class, "LBL_BrowseButton")); // NOI18N
        removeButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SchemaPanel.class, "LBL_RemoveButton")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SchemaPanel.class, "PROP_schema_panel_name")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SchemaPanel.class, "PROP_schema_panel_name")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private boolean isDuplicate(String schemaFileName) {
        if(rows.size() == 1)
            return false;
       for(int i=0 ; i < rows.size(); i++ ){
           List rowData = (List)rows.get(i);
           SchemaObject obj = (SchemaObject)rowData.get(SCHEMA_COL);
           if(obj.toString().equals(startString))
               continue;
           if(obj.getSchemaFileName().equals(schemaFileName))
               return true;
       }
        return false;
    }

private void primarySchemaCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_primarySchemaCheckBoxActionPerformed
 //   fireChange();

}//GEN-LAST:event_primarySchemaCheckBoxActionPerformed

private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
     int row = schemaTable.getSelectedRow();
     //System.out.println("delete row = " + row);
     deleteRow(row);
}//GEN-LAST:event_removeButtonActionPerformed

   private void browseButtonActionPerformed(ActionEvent evt){
      gui = new SchemaImportGUI(templateWizard);
       final DialogDescriptor descriptor = new DialogDescriptor(gui,
                NbBundle.getMessage(SchemaPanel.class,"LBL_Browser"),
                true, this);
        Dialog dlg = DialogDisplayer.getDefault().createDialog(descriptor);
        dlg.getAccessibleContext().setAccessibleDescription("DSC_Browser");
        dlg.setVisible(true);
   }
    private void initAccessibility() {

        // memonics
        Util util = Util.THIS;        
        locationLabel.setDisplayedMnemonic(util.getChar(
                SchemaPanel.class, "PROP_schema_locationLabel_mne"));
        browseButton.setMnemonic(util.getChar(
                SchemaPanel.class, "LBL_BrowseButton_mme"));
        removeButton.setMnemonic(util.getChar(
                SchemaPanel.class, "LBL_RemoveButton_mne"));
       
    }
    
    /**
     * Update namespace and root combo models.
     */
    private void updatePossibilities() {
    }
    
    /** User just entered the panel, init view by model values
     */
    protected void initView() {
                
    }    
    
    /** User just leaved the panel, update model
     */
    protected void updateModel() {
         int numRows = tableModel.getRowCount();
         int numCols = tableModel.getColumnCount();
         String uri = null;
         List schemaFiles = new ArrayList();
         for (int i=0; i < numRows; i++) {
             List rowData = (List)rows.get(i);
             boolean primary= ((Boolean)rowData.get(PRIMARY_COL)).booleanValue();
             String prefix = (String)rowData.get(PREFIX_COL);
             SchemaObject obj = (SchemaObject)rowData.get(SCHEMA_COL);
             if(obj.toString().equals(startString))
                 continue;
             //set the prefix since this is the most updated prefix
             obj.setPrefix(prefix);
             String root = (String)rowData.get(ROOT_COL);
             if(primary) {
                 model.setPrimarySchema(obj.toString());
                 model.setNamespace(obj.getNamespace());
                 model.setPrefix(prefix);
                 model.setRoot(root);
                 File file = new File(obj.toString());
                 uri = file.getName();
                 if (uri != null) {
                    // we need to escape spaces, URI does not like them
                    uri = uri.replace(" ", "%20"); // NOI18N
                    try {
                        // escape the non-ASCII characters
                        uri = new URI(uri).toASCIIString();
                      } catch (URISyntaxException e) {
                         // the specified uri is not valid, it is too late to fix it now
                      }
               }                
              model.setSystemID(uri == null || uri.length() == 0 ? null : uri);
             }
             schemaFiles.add(obj);
         }
         model.setSchemaNodes(schemaFiles);
           
    }    
    
    /** User just reentered the panel.
     */
    protected void updateView() {
    }

    private String getStartString() {
        return NbBundle.getMessage(SchemaPanel.class, "LBL_TABLE_SCHEMA_PROMPT");
    }
    
    private void tableKeyPressed(KeyEvent evt) {
        if( evt.getKeyCode()==KeyEvent.VK_DELETE ){
            int row = schemaTable.getSelectedRow();
          //  System.out.println("delete row = " + row);
            deleteRow(row);
        } 
    }
    
    private void deleteRow(int index)  {
        if(index!=-1){ 
            SchemaObject val = (SchemaObject)tableModel.getValueAt(index, SCHEMA_COL);
            if(val.toString().equals(startString))
                return;
            rows.remove(index);
            schemaTable.addNotify();
            fireChange();
       }

   }
        
      
  

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JPanel locationPanel;
    private javax.swing.JButton removeButton;
    private javax.swing.JTable schemaTable;
    // End of variables declaration//GEN-END:variables

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(DialogDescriptor.OK_OPTION)) {
            List nodes = gui.getSelectedNodes();
            if(nodes != null){
                String noRoot = NbBundle.getMessage(SchemaPanel.class, "LBL_No_Root_Elements");
                for(int i=0;i < nodes.size(); i++ ){
                    List row = new ArrayList();
                    ExternalReferenceDataNode erdn = (ExternalReferenceDataNode)nodes.get(i);
                    DataObject dobj = (DataObject) erdn.getLookup().lookup(DataObject.class);
                    if(isDuplicate(erdn.getSchemaFileName()))
                        continue;
                    FileObject fobj = dobj.getPrimaryFile();
                    SchemaObject obj = new SchemaObject(erdn.getSchemaFileName());
                    obj.setFromCatalog(erdn.isResolveThroughCatalog());
                    obj.setNamespace(erdn.getNamespace());
                    obj.setSchemaFileName(erdn.getSchemaFileName());
                    
                    row.add(false);
                    row.add(obj);
                   
                    SchemaParser.SchemaInfo info = SchemaParser.getRootElements(fobj);
                    if (info != null && info.roots.size() > 0) {
                        Iterator it = info.roots.iterator();
                        String[] rootElements = new String[(info.roots.size())];
                        info.roots.toArray(rootElements);
                        obj.setRootElements(rootElements);
                        row.add(rootElements[0]);
                    } else {
                        //we have to add some dummy element since there are no roots
                        row.add(noRoot);
                    }
                    if (null != info.namespace) {
                        String pre = generateUniquePrefix();
                        obj.setPrefix(pre);
                        //keep track of unique prefixes
                        //addPrefix(pre);
                        row.add(pre);
                    } else {
                        row.add(null);
                    }
                    tableModel.addRow(0,row);
               }
               schemaTable.addNotify();
            }
        } else if(evt.getSource().equals(DialogDescriptor.CANCEL_OPTION)){
            gui.setVisible(false);
        }
    }

    private List createBlankElement(String val) {
        List t = new ArrayList();
        t.add(false);
        t.add(new SchemaObject(val));
        t.add((String) " ");        
        t.add((String) " ");
        return t;
    }
    
    private void addRow(String val) {
        List r=createBlankElement(val);
        tableModel.addRow(r);
        schemaTable.addNotify();

   }
    
    private void initComp() {
        schemaTable.getModel().addTableModelListener(this);
        schemaTable.getTableHeader().setReorderingAllowed( false );
        startString = getStartString();
        
        //set key listener to delete rows when user presses del key
        schemaTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                tableKeyPressed(evt);
            }           
        });
        //add the initial row
        addRow(startString);
        
        //set width
        TableColumn column = null;
        for (int i = 0; i < 4; i++) {
            column = schemaTable.getColumnModel().getColumn(i);
            if (i == PRIMARY_COL) {
                   column.setPreferredWidth(40); 
            } else if(i ==SCHEMA_COL) {
                column.setPreferredWidth(250);
           } else {
                column.setPreferredWidth(80);
           }
         }
        
        //set renderer and editor for the first column
        schemaTable.getColumnModel().getColumn(PRIMARY_COL).setCellRenderer(new RadioColumnRenderer());
        schemaTable.getColumnModel().getColumn(PRIMARY_COL).setCellEditor(new RadioColumnEditor());       
        
        //set up rendere/editor for the combo box column
         TableColumn rootColumn = schemaTable.getColumnModel().getColumn(ROOT_COL);
         rootColumn.setCellEditor(new ComboBoxColumnEditor());
         DefaultTableCellRenderer renderer =  new DefaultTableCellRenderer();
         renderer.setToolTipText(NbBundle.getMessage(SchemaPanel.class, "TIP_COMBO_COL"));
         rootColumn.setCellRenderer(renderer);
         
         int height = schemaTable.getRowHeight();
    
        // Determine highest cell in the row
        for (int c=0; c<schemaTable.getColumnCount(); c++) {
            TableCellRenderer r = schemaTable.getCellRenderer(0, c);
            Component comp = schemaTable.prepareRenderer(r, 0, c);
            int h = comp.getPreferredSize().height;
            height = Math.max(height, h);
        }
         schemaTable.setRowHeight(height);
    }
    
    public void tableChanged(TableModelEvent e) {
        //System.out.println("TBALE changed");
        //boolean prefixFlag = false;
        int row = e.getFirstRow();
        int column = e.getColumn();
        AbstractTableModel tblModel = (AbstractTableModel) e.getSource();
        Object data = tblModel.getValueAt(row, column);
        if(column == SCHEMA_COL) {
            SchemaObject rowValue = (SchemaObject)data;
            if(rowValue.toString().equals(startString))
                return;
            String genPrefix = (String) tblModel.getValueAt(row, PREFIX_COL);
            if (genPrefix == null || genPrefix.equals(" ")  ) {
                String prefix = generateUniquePrefix();               
                tableModel.setValueAt(prefix, row, PREFIX_COL);                 
            }
            if(row == tableModel.getRowCount() - 1) {
                addRow(startString);
            }
            //if its the first row, then select it as primary
            if(row == 0) {
               // System.out.println("added first row");
                tblModel.setValueAt(true, 0, 0);
            }
        } 
    }
    
    private String generateUniquePrefix() {
        int counter = 1;
        String generatedName = PREFIX + counter++;
        
        while(! isPrefixUnique(generatedName)) {
            generatedName = PREFIX + counter++;
        } 
        return generatedName;    
    }
/*
    private void addPrefix(String prefix) {
        if (prefix == null) return;

        prefixSet.add(prefix.trim());
    }
*/
    
    private boolean isPrefixUnique(String prefix) {
        Set<String> prefixSet = new HashSet<String>();
        for (int i = 0; i < tableModel.getRowCount(); ++i) {
            Object prefixValue = tableModel.getValueAt(i, PREFIX_COL);
            if (prefixValue instanceof String) {
                prefixSet.add((String) prefixValue);
            }
        }
        return (! prefixSet.contains(prefix));
    }
    
    public boolean isPanelValid() {
        //activate next/finish button only when a primay schema has been selected
        //and it has a root element
        if(tableModel.getRowCount() == 1)
            return false;
        int rowCount = tableModel.getRowCount();
        for(int i=0; i < rowCount; i++) {
            List rowValue = (List)rows.get(i);
            boolean selected = ((Boolean)rowValue.get(PRIMARY_COL)).booleanValue();
            if(selected){
                return true;
            }
        }
        return false;
    }
    
    class SchemaTableModel extends AbstractTableModel {
        

        public int getColumnCount() {
            return 4;
        }

       public int getRowCount() {
            return rows.size();
        }

        @Override
        public String getColumnName(int col) {
            String colName = null ;
            switch(col) {
                case 0:
                    colName = NbBundle.getMessage(SchemaPanel.class, "LBL_PRIMARY_COL");
                    break;
                case 1:
                    colName= NbBundle.getMessage(SchemaPanel.class, "LBL_SCHEMA_COL");
                    break;
                case 2: 
                    colName= NbBundle.getMessage(SchemaPanel.class, "LBL_ROOT_COL");
                    break;
                case 3:
                    colName= NbBundle.getMessage(SchemaPanel.class, "LBL_PREIFX_COL");
                    break;
            }
            return colName;         
        }

     // @Override
        public Object getValueAt(int row, int col) {
            List obj = (List)rows.elementAt(row);
            if(col == SCHEMA_COL){
                SchemaObject val = (SchemaObject)obj.get(col);
                return val;
            } else if (col == PREFIX_COL) {
                return obj.get(col) == null ? NbBundle.getMessage(SchemaPanel.class, "LBL_NoPrefix") : obj.get(col);
            } else {            
                return obj.get(col);
            }
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        @Override
        public boolean isCellEditable(int row, int col) {
            if(col == ROOT_COL ) {
                   return true;
            }else if(col == PRIMARY_COL){
                SchemaObject s = (SchemaObject)getValueAt(row,SCHEMA_COL);
                if(s.toString().equals(startString))
                    return false;
                else
                    return true;
            } else if (col == PREFIX_COL) {
                SchemaObject s = (SchemaObject)getValueAt(row,SCHEMA_COL); 
                return s.getNamespace() != null;
            }   else
                return true;
         
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        @Override
        public void setValueAt(Object value, int row, int col) {
          /*  if(value != null)
                         System.out.println("Setting value at " + row + "," + col
                                   + " to " + value
                                   + " (an instance of "
                                   + value.getClass() + ")");*/
            
            if (row < 0 || col < 0 || row>= getRowCount() || col >= getColumnCount()) 
                return;
            if(value == null)
                return;
            
            List rowVector = (List)rows.elementAt(row);
            
            if (col == PRIMARY_COL  &&  ((Boolean)value).booleanValue() ) {
                    //only those schemas can be set as primary that have root elements
                    SchemaObject obj = (SchemaObject)getValueAt(row, SCHEMA_COL);
                    if(obj.getRootElements()  == null || obj.getRootElements().length == 0) {
                        String errMsg =  org.openide.util.NbBundle.getMessage(SchemaPanel.class, "MSG_SchemaPanel_Incorrect_Primary");
                        templateWizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, errMsg);
                        return;
                    } else
                        templateWizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, "");
                
		    for (int i = 0; i < getRowCount(); i++) {
			if (i != row) {
		            setValueAt(false, i, 0);
                        }
                    }
                    rowVector.set(col, value);
                
            } else if(col == SCHEMA_COL){
                String systemId = (String)value;
                
                //if the value being set is the same as start string, ignore
                //if the value is not a url, ignore
                if(systemId.equals(startString))
                    return;
                try {
                    File file = new File(systemId);
                    if( !file.exists() ) {
                        if( !systemId.startsWith("http") )
                            return;
                    }
                    URL context = model.getTargetFolderURL();   
                        if (context != null) {
                            systemId = new URL(context, systemId).toExternalForm();                    
                    }
               } catch (MalformedURLException ex) {
                   return;
               }
                
                //create a schema object
               
                    SchemaParser parser = new SchemaParser();
                    SchemaParser.SchemaInfo info = parser.parse(systemId);
                    SchemaObject obj = new SchemaObject ((String)value);
                    if (info.namespace != null) {
                        obj.setNamespace(info.namespace);
                    }
                    if (info != null && info.roots.size() > 0) {
                        Iterator it = info.roots.iterator();
                        String[] rootElements = new String[(info.roots.size())];
                        info.roots.toArray(rootElements);
                        obj.setRootElements(rootElements);  
                        rowVector.set(ROOT_COL, rootElements[0]);
                    }
                    obj.setSchemaFileName((String)value);
                    rowVector.set(col, obj);
               
               
            } else if (col == PREFIX_COL) {
                String prefix = ((String) value).trim();

//              if (prefix.trim().length() == 0)
//                  return;
//              if (verifyUniquePrefix(prefix)) {
//                   addPrefix(prefix);
//                   rowVector.set(col, value);
//               }

                if (isPrefixUnique(prefix)) {
                    rowVector.set(col, prefix);
                }
            } else {
                rowVector.set(col, value);
            }
            templateWizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, "");
            fireChange();
            fireTableCellUpdated(row, col);
      }

        private void addRow(List r) {
            rows.add(r);
        }

        private void addRow(int i, List row) {
            rows.add(i, row);
        }

        private void printDebugData() {
            int numRows = getRowCount();
            int numCols = getColumnCount();

            for (int i=0; i < numRows; i++) {
                System.out.print("    row " + i + ":");
                for (int j=0; j < numCols; j++) {
                 //   System.out.print("  " + data[i][j]);
                }
                System.out.println();
            }
            System.out.println("--------------------------");
        }
        
    }
  
    class RadioColumnEditor extends AbstractCellEditor implements TableCellEditor {
         // protected EventListenerList listenerList = new EventListenerList();
        //  protected ChangeEvent changeEvent = new ChangeEvent(this);
          private JRadioButton theRadioButton;
          
       public RadioColumnEditor() {
			super();
			theRadioButton = new JRadioButton();
			theRadioButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
                                    fireEditingStopped();
				}
			});
		}
      public Component getTableCellEditorComponent(JTable table, Object obj, boolean isSelected, int row, int col) {          
			theRadioButton.setHorizontalAlignment(SwingUtilities.CENTER);
                        if(obj != null){
			    Boolean lValueAsBoolean = (Boolean)obj;
			    theRadioButton.setSelected(lValueAsBoolean.booleanValue());
                        }
			return theRadioButton;
		}
 
        public Object getCellEditorValue() {
            return theRadioButton.isSelected();
        }
    }
    
    class RadioColumnRenderer extends JRadioButton implements TableCellRenderer {
          public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)      {
              this.setBackground(Color.WHITE);
              if (value == null){
                  this.setSelected(false);
              } else{
                  Boolean ValueAsBoolean = (Boolean)value;
                  this.setSelected(ValueAsBoolean.booleanValue());
              }
              this.setHorizontalAlignment(SwingUtilities.CENTER);
              setToolTipText(NbBundle.getMessage(SchemaPanel.class, "TIP_PREFIX_COL")); 
              return this;
        }
      }
    
    class ComboBoxColumnEditor extends AbstractCellEditor implements TableCellEditor {
         private JComboBox comboBox;
          
         public ComboBoxColumnEditor() {
			super();			
		}
        public Component getTableCellEditorComponent(JTable table, Object obj, boolean isSelected, int row, int col) {  
                        comboBox = new JComboBox();   
                        comboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
                                    fireEditingStopped();
				}
			});
                        DefaultComboBoxModel rootModel = new DefaultComboBoxModel();
                        SchemaObject o = (SchemaObject)table.getModel().getValueAt(row, SCHEMA_COL);
                        
                        if( !(o.toString().equals(startString))) {
                            String[] root = o.getRootElements();
                            if(root != null && root.length >0) {
                                for(int i=0; i < root.length; i++)
                                    rootModel.addElement(root[i]);
                            }
                        }                           
                        comboBox.setModel(rootModel);
			return comboBox;
		}
 
      public Object getCellEditorValue() {
                     return comboBox.getModel().getSelectedItem();
			//return new Boolean(theRadioButton.isSelected());
		}

      
      }


}
