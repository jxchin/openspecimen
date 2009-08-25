/**
 * 
 */

package edu.wustl.catissuecore.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;

import edu.wustl.catissuecore.bean.GenericSpecimen;
import edu.wustl.catissuecore.bean.SpecimenDetailsInfo;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;

/**
 * This tag will accept the list of specimens and display them in 
 * editable or readonly mode.
 * It accepts following attributes:
 * 	columnHeaderListName
 * 	formName
 * 	dataListName
 * 	dataListType
 * 	columnListName
 * 	isReadOnly
 * 	displayColumnListName
 * 
 * @author mandar_deshmukh
 *
 */
public class SpecimenDetailsNewFormat extends TagSupport
{

	private transient final Logger logger = Logger.getCommonLogger(SpecimenDetailsNewFormat.class);
	private static final long serialVersionUID = 1234567890L;
	// data type list used to display different types of data.
	private transient final String dataListTypes[] = {"Parent", "Aliquot", "Derived"};

	public static final String[] COLUMN_NAMES = {"Parent", "Label", "Barcode", "Type", "Quantity",
			"Concentration", "Location", "Collected", "PrintLabel"};

	public static final String[] COLUMN_LABELS = {"specimen.label", "specimen.barcode",
			"specimen.subType", "anticipatorySpecimen.Quantity",
			"anticipatorySpecimen.Concentration", "anticipatorySpecimen.Location",
			"anticipatorySpecimen.Collected", "specimen.printLabel"};

	// ----------- Mandar : 2Dec08 for New UI format start -----------------------------
	public static final String[] HDR1_COLS = {"Parent", "Label", "Barcode", "Type", "Quantity",
			"Concentration", "Location", "Collected", "PrintLabel"};
	public static final String[] HDR2_COLS = {"Type", "Pathological Status", "Tissue Side",
			"Tissue Site"};

	public static final String[] H1COL_LBLS = {"specimen.label", "specimen.barcode",
			"specimen.subType", "anticipatorySpecimen.Quantity",
			"anticipatorySpecimen.Concentration", "anticipatorySpecimen.Location",
			"anticipatorySpecimen.Collected", "specimen.printLabel"};

	public static final String[] H2COL_LBLS = {"specimen.subType", "specimen.pathologicalStatus",
			"specimen.tissueSide", "specimen.tissueSite"};

	// ----------- Mandar : 2Dec08 for New UI format end -------------------------------

	private static transient final String TR_OPEN = "<TR>";
	private static transient final String TR_CLOSE = "</TR>";
	private static transient final String TD_OPEN = "<TD>";
	private static transient final String TD_CLOSE = "</TD>";
	private static transient final String STYLE_CLASS = "black_ar";
	private static transient final String SPACE = "&nbsp;";
	private static transient final String TR_GRAY = "<TR class='tr_anti_bg_gray'>";
	private static transient final String TR_BLUE = "<TR class='tr_anti_bg_blue'>";
	private static transient final String TD_1HLF = "<TD width='";
	private static transient final String TD_2HLF = "%'>";

	//--------------- TAG Attribute Section start [Will be provided by the user of the tag thru the TAG.]------------
	private String displayColumnListName = "";
	private String columnHeaderListName = "";
	private String dataListName = "";
	private String isReadOnly = ""; // ------- as decided 
	private String formName = "";
	private String dataListType = "";
	private String columnListName = "";
	//--------------- TAG Attribute Section end ------------

	//---------------  Attribute Section start ------------
	private transient List columnList = null; // List containing column names

	private transient List columnHeaderList = null;
	private transient List displayColumnList = null; // List of columns to show. If name not present hide the column.
	private transient List dataList = null;
	private transient boolean showParentId = false;
	private String elementPrefixPart1 = "";
	private transient String functionCall = "";

	private transient int xtra = 0;
	private transient int colNum = 0;
	private transient boolean isParentList = false;
	private transient int pWd = 10;
	private transient int cWd = 10;
	SpecimenDetailsInfo specimenSummaryForm = null;

	//	--------------- Attribute Section end ------------

	// ------------------Getter - Setter Section start ---------
	public String getColumnHeaderListName()
	{
		return this.columnHeaderListName;
	}

	public void setColumnHeaderListName(String columnHeaderListName)
	{
		this.columnHeaderListName = columnHeaderListName;
	}

	public String getDataListName()
	{
		return this.dataListName;
	}

	public void setDataListName(String dataListName)
	{
		this.dataListName = dataListName;
	}

	public String getDisplayColumnListName()
	{
		return this.displayColumnListName;
	}

	public void setDisplayColumnListName(String displayColumnListName)
	{
		this.displayColumnListName = displayColumnListName;
	}

	public String getIsReadOnly()
	{
		return this.isReadOnly;
	}

	public void setIsReadOnly(String isReadOnly)
	{
		this.isReadOnly = isReadOnly;
	}

	public String getFormName()
	{
		return this.formName;
	}

	public void setFormName(String formName)
	{
		this.formName = formName;
	}

	public String getDataListType()
	{
		return this.dataListType;
	}

	public void setDataListType(String dataListType)
	{
		this.dataListType = dataListType;
	}

	public String getColumnListName()
	{
		return this.columnListName;
	}

	public void setColumnListName(String columnListName)
	{
		this.columnListName = columnListName;
	}

	// ------------------Getter - Setter Section end ---------

	/**
	 * A call back function, which gets executed by JSP runtime when opening tag for this
	 * custom tag is encountered. 
	 */
	public int doStartTag() throws JspException
	{
		try
		{
			final JspWriter out = this.pageContext.getOut();

			out.print("");
			if (this.validateTagAttributes())
			{
				this.initialiseElements();
				if (this.validateLists())
				{
					//					out.print(generateHeaderForDisplay());
					out.print(this.generateRowOutput());
				}
				else
				{
					out
							.print("<b>Column header list is not matching with the display column list.</b>");
				}
			}
			else
			{
				out
						.print("<b>Some of the attributes of the tag are missing or are not proper.</b>");
			}
		}
		catch (final IOException ioe)
		{
			this.logger.debug(ioe.getMessage(), ioe);
			throw new JspTagException("Error:IOException while writing to the user");
		}
		return SKIP_BODY;
	}

	/**
	 * A call back function
	 */
	public int doEndTag() throws JspException
	{
		this.displayColumnListName = "";
		this.columnHeaderListName = "";
		this.dataListName = "";
		this.isReadOnly = "";
		this.formName = "";
		this.dataListType = "";
		this.columnList = null;
		this.columnHeaderList = null;
		this.displayColumnList = null;
		this.dataList = null;
		this.showParentId = false;
		this.elementPrefixPart1 = "";
		this.specimenSummaryForm = null;
		this.functionCall = "";
		this.xtra = 0;
		this.colNum = 0;
		this.pWd = 10;
		this.cWd = 10;

		return EVAL_PAGE;
	}

	/* method to validate the given values for the attributes.
	 * Returns true if all required attributes are in proper valid format. Otherwise returns false. 
	 */
	private boolean validateTagAttributes()
	{
		boolean result = true;
		final ServletRequest request = this.pageContext.getRequest();
		ActionErrors errors = (ActionErrors) request.getAttribute(Globals.ERROR_KEY);
		if (errors == null)
		{
			errors = new ActionErrors();
		}
		if (this.columnHeaderListName == null || this.columnHeaderListName.trim().length() < 1)
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
					"Column Header List Name is null or empty"));
			result = false;
		}
		if (this.isReadOnly == null || this.isReadOnly.trim().length() < 1)
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
					"DisplayOnly parameter is null or empty"));
			result = false;
		}
		else if (!Constants.TRUE.equalsIgnoreCase(this.isReadOnly)
				&& (this.formName == null || this.formName.trim().length() < 1))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("Form name is null or empty"));
			result = false;
		}
		if (this.dataListName == null || this.dataListName.trim().length() < 1)
		{
			errors.add(ActionErrors.GLOBAL_ERROR,
					new ActionError("Data List Name is null or empty"));
			result = false;
		}
		if (this.dataListType == null || this.dataListType.trim().length() < 1)
		{
			errors.add(ActionErrors.GLOBAL_ERROR,
					new ActionError("Data List Type is null or empty"));
			result = false;
		}
		else if (!this.isListDataTypeOK())
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("Data List Type is invalid"));
			result = false;
		}
		if (this.displayColumnListName == null || this.displayColumnListName.trim().length() < 1)
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
					"Display Column List Name is null or empty"));
			result = false;
		}

		request.setAttribute(Globals.ERROR_KEY, errors);
		return result;
	}

	private void initialiseElements()
	{
		final ServletRequest request = this.pageContext.getRequest();

		this.specimenSummaryForm = (SpecimenDetailsInfo) request.getAttribute(this.formName);
		//		columnHeaderList = (List)request.getAttribute(columnHeaderListName);
		this.dataList = (List) request.getAttribute(this.dataListName);
		//		displayColumnList = (List)request.getAttribute(displayColumnListName);
		//		columnList = (List)request.getAttribute(columnListName);
		//		if(columnHeaderList == null || columnHeaderList.isEmpty())
		{
			this.columnHeaderList = new ArrayList();
			if (this.dataListType.equalsIgnoreCase(this.dataListTypes[0]))
			{
				this.columnHeaderList.add("");
			}
			else
			{
				this.columnHeaderList.add("anticipatorySpecimen.Parent");
			}

			for (final String element : SpecimenDetailsNewFormat.COLUMN_LABELS)
			{
				this.columnHeaderList.add(element);
			} // 0

		}

		if (!this.dataListType.equalsIgnoreCase(this.dataListTypes[0]))
		{
			this.showParentId = true;
		}
		//		if(displayColumnList == null || displayColumnList.isEmpty())
		{
			this.displayColumnList = new ArrayList();
			this.setFixedColumnsList(this.displayColumnList);
		}
		//		if(columnList == null || columnList.isEmpty())
		{
			this.columnList = new ArrayList();
			this.setFixedColumnsList(this.columnList);
		}
	}

	private boolean isListDataTypeOK()
	{
		boolean result = false;
		for (final String dataListType2 : this.dataListTypes)
		{
			if (this.dataListType.equalsIgnoreCase(dataListType2))
			{
				if (this.dataListTypes[0].equalsIgnoreCase(dataListType2))
				{
					this.elementPrefixPart1 = "specimen[";
					this.colNum = 8;
				}
				else
				{
					this.elementPrefixPart1 = dataListType2.toLowerCase().trim() + "[";
					this.colNum = 9;
				}
				result = true;
			}
		}
		return result;
	}

	private List getDataList()
	{
		List lst = new ArrayList();
		//setting the data list to be used to display the specimens.
		if (this.dataList == null || this.dataList.isEmpty())
		{
			if (this.specimenSummaryForm != null)
			{
				if (this.dataListType.equalsIgnoreCase(this.dataListTypes[0]))
				{
					lst = this.specimenSummaryForm.getSpecimenList();
				}
				else if (this.dataListType.equalsIgnoreCase(this.dataListTypes[1]))
				{
					lst = this.specimenSummaryForm.getAliquotList();
				}
				else if (this.dataListType.equalsIgnoreCase(this.dataListTypes[2]))
				{
					lst = this.specimenSummaryForm.getDerivedList();
				}
			}
		}
		else
		// get the list from ViewSpecimenSummaryForm based on list type
		{
			lst = this.dataList;
		}
		if (this.dataListType.equalsIgnoreCase(this.dataListTypes[0]))
		{
			this.xtra = 6;
			this.isParentList = true;
			this.pWd = 5;
			this.cWd = 12;
		}
		if (this.dataListType.equalsIgnoreCase(this.dataListTypes[2]))
		{
			this.xtra = 3;
		}

		return lst;
	}

	private String getFormattedValue(Object obj)
	{
		String str = "";
		if (obj == null || obj.toString().trim().length() == 0)
		{
			str = "";
		}
		else
		{
			str = obj.toString();
		}
		return str;
	}

	private String getHTMLFormattedValue(Object obj)
	{
		final String str = this.getFormattedValue(obj);
		return (str.trim().length() > 0 ? str : SPACE);
	}

	private boolean validateLists()
	{
		boolean result = true;
		final ServletRequest request = this.pageContext.getRequest();
		ActionErrors errors = (ActionErrors) request.getAttribute(Globals.ERROR_KEY);
		if (errors == null)
		{
			errors = new ActionErrors();
		}
		if (this.columnHeaderList.size() != this.displayColumnList.size())
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
					"Column Header List is not matching the Display Column List."));
			result = false;
		}
		request.setAttribute(Globals.ERROR_KEY, errors);
		return result;
	}

	private void setFixedColumnsList(List list)
	{
		if (list == null)
		{
			list = new ArrayList();
		}
		for (final String element : SpecimenDetailsNewFormat.COLUMN_NAMES)
		{
			list.add(element);
		}
	}

	/*
	 * Method to generate row output for generic specimen
	 */
	private String generateRowOutput() throws IOException
	{
		final StringBuffer sb = new StringBuffer();
		sb.append("");
		final List specimenList = this.getDataList();
		if (this.dataListTypes[1].equalsIgnoreCase(this.dataListType))
		{
			sb.append("<TABLE border=0 width='100%'>");
		}
		for (int counter = 0; counter < specimenList.size(); counter++)
		{
			final GenericSpecimen specimen = (GenericSpecimen) specimenList.get(counter);
			if (Constants.TRUE.equalsIgnoreCase(this.isReadOnly) || specimen.getReadOnly())
			{
				//				 addReadOnlyRow(sb, counter, specimen);
				this.addEditableRow(sb, counter, specimen, true);
			}
			else
			{
				this.addEditableRow(sb, counter, specimen, false);
			}
		} // outer most loop for specimenList
		sb.append("");
		if (this.dataListTypes[1].equalsIgnoreCase(this.dataListType))
		{
			sb.append("</TABLE>");
		}

		//String output =sb.toString();				
		return sb.toString();
	}

	private void addEditableRow(StringBuffer sb, int counter, GenericSpecimen specimen,
			boolean isTextRow)
	{
		// ------------ Mandar : 2Dec08 New Code for new formatUI start----------------
		if (this.dataListTypes[1].equalsIgnoreCase(this.dataListType)) // for aliquots
		{
			final String elementNamePrefix = this.elementPrefixPart1 + counter + "].";
			if (counter == 0)
			{
				this.createHeaderRow1(sb, TR_GRAY, specimen); // row1 containing headers for first half (editable fields) 
			}
			this.createFieldRow(sb, counter, specimen, elementNamePrefix, isTextRow); // row2 containing actual editable fields (first half) 
		}
		else
		{
			final String elementNamePrefix = this.elementPrefixPart1 + counter + "].";
			if ((counter + 1) % 2 == 0)
			{
				sb.append(TR_BLUE);
			}
			else
			{
				sb.append(TR_GRAY);
			}

			sb.append(TD_OPEN);
			sb.append("<TABLE border=0 width='100%'>");
			this.createHeaderRow1(sb, TR_OPEN, specimen); // row1 containing headers for first half (editable fields)
			this.createFieldRow(sb, counter, specimen, elementNamePrefix, isTextRow); // row2 containing actual editable fields (first half) 
			this.createHeaderRow2(sb); // row3 containing headers for second half (text fields)
			this.createTextFieldRow(sb, counter, specimen, elementNamePrefix); // row containing text data (second half)

			sb.append("</TABLE>");
			sb.append(TD_CLOSE);
			sb.append(TR_CLOSE);
		}

		// ------------ Mandar : 2Dec08 New Code for new formatUI end----------------

	}

	// ------------ Mandar : 2Dec08 New Code for new formatUI start----------------
	private void createParentComponent(StringBuffer sb, GenericSpecimen specimen,
			String elementNamePrefix, boolean isTextRow)
	{
		final String nameValue[] = this.get1EleDetAt(0, specimen, elementNamePrefix);
		if (!this.showParentId)
		{
			sb.append("<TD rowspan=3 width='" + this.pWd + "%'>");
			nameValue[1] = this.getFormattedValue(specimen.getUniqueIdentifier());
			this.createParentRadioComponent(sb, nameValue);
		}
		else
		{
			sb.append(TD_1HLF + this.pWd + TD_2HLF);
			if (isTextRow)
			{
				sb.append("<SPAN class=" + STYLE_CLASS + ">" + nameValue[1] + "</SPAN>");
			}
			else
			{
				this.createTextComponent(sb, nameValue, STYLE_CLASS, 8);
			}
		}
		sb.append(TD_CLOSE);
	}

	private void createParentRadioComponent(StringBuffer sb, String[] nameValue)
	{
		//		 sb.append("<td class=\"black_ar_md\" >");
		if (this.specimenSummaryForm.getSelectedSpecimenId().equalsIgnoreCase(nameValue[1]))
		{
			sb.append("<input type=\"radio\" name=\"selectedSpecimenId\" value=\"" + nameValue[1]
					+ "\" checked=\"checked\" onclick=\"onParentRadioBtnClick()\">");
		}
		else
		{
			sb.append("<input type=\"radio\" name=\"selectedSpecimenId\" value=\"" + nameValue[1]
					+ "\" onclick=\"onParentRadioBtnClick()\">");
		}
		//		 sb.append(TD_CLOSE);
	}

	private void createTextComponent(StringBuffer sb, String[] nameValue, String styleClass,
			int size)
	{
		//		 sb.append("<td class=\"black_ar_md\" >"); 
		sb.append("<input type=\"text\" name=\"" + nameValue[0] + "\" value=\"" + nameValue[1]
				+ "\" class=\"" + styleClass + "\" size=\"" + size + "\">");
		//		 if(nameValue.length == 3)
		//		 {
		//			 sb.append("<BR><SPAN nowrap>"+nameValue[2]+"</SPAN>");
		//		 }
		//	 sb.append(TD_CLOSE);
	}

	private void createHeaderRow1(StringBuffer sb, String tr, GenericSpecimen specimen)
	{
		sb.append(tr);
		int tmpd = 0, tmpcwd = 0;
		if (this.dataListType.equalsIgnoreCase(this.dataListTypes[2]))
		{
			tmpd = 3;
		}
		tmpcwd = this.cWd;
		for (int cnt = 0; cnt < this.columnHeaderList.size(); cnt++)
		{
			if (cnt == 5)//Concentration
			{
				this.cWd = this.cWd - tmpd;
			}
			else
			{
				this.cWd = tmpcwd;
			}
			if ((((String) this.columnHeaderList.get(cnt)).trim().length() > 0))
			{
				// to be displayed only in case of aliquots.	|| 		
				if ((cnt == 3 && !this.dataListType.equalsIgnoreCase(this.dataListTypes[1]))
						|| (cnt == 5 && !this.dataListType.equalsIgnoreCase(this.dataListTypes[2]))) // to be displayed only in case of derived.
				{
					continue;
				}
				else if ((cnt == 1 && !(this.specimenSummaryForm.getShowLabel() && specimen
						.getShowLabel()))
						|| (cnt == 2 && !(this.specimenSummaryForm.getShowbarCode() && specimen
								.getShowBarcode())))
				{
					sb.append(TD_1HLF + "1" + TD_2HLF);
					sb.append(SPACE);
				}
				else if (cnt == 6)//Location
				{
					sb.append("<TD colspan=4 width=" + (50 - this.pWd + tmpd) + "%>");// 3 35
					sb.append("<SPAN class=black_ar_b>"
							+ ApplicationProperties.getValue((String) this.columnHeaderList.get(6))
							+ "</SPAN>");
				}
				else if (cnt == 7 && !this.specimenSummaryForm.getShowCheckBoxes())//Collected
				{
					sb.append(TD_1HLF + 3 + TD_2HLF);//bug 11169
					sb.append(SPACE);
				}
				else if (cnt == 8)//print bug 11169
				{
					sb.append(TD_1HLF + 3 + TD_2HLF);
					sb.append("<center><SPAN class=black_ar_b>"
							+ ApplicationProperties.getValue((String) this.columnHeaderList.get(8))
							+ "</SPAN></center>");
				}
				else
				{
					//bug 11169 start
					if (cnt == 7)
					{
						sb.append(TD_1HLF + 3 + TD_2HLF);
						sb.append("<center><SPAN class=black_ar_b>"
								+ ApplicationProperties.getValue((String) this.columnHeaderList
										.get(cnt)) + "</SPAN></center>");
					}
					else
					{
						sb.append(TD_1HLF + this.cWd + TD_2HLF);
						sb.append("<SPAN class=black_ar_b>"
								+ ApplicationProperties.getValue((String) this.columnHeaderList
										.get(cnt)) + "</SPAN>");
					}
					//bug 11169 end
				}

			}
			else
			{
				sb.append(TD_1HLF + this.pWd + TD_2HLF);
				sb.append(SPACE);
			}
			sb.append(TD_CLOSE);
		}
		sb.append(TR_CLOSE);
	}

	private void createHeaderRow2(StringBuffer sb)
	{
		String colspan = "";
		int cols = 0;
		int colsp = 1;
		if (this.dataListType.equalsIgnoreCase(this.dataListTypes[2]))
		{
			colspan = "colspan=2";
			colsp = 2;
		}
		sb.append(TR_OPEN);
		for (int cnt = 0; cnt < H2COL_LBLS.length; cnt++)
		{
			if (this.dataListType.equalsIgnoreCase(this.dataListTypes[2]) && cnt > 1)
			{
				sb.append("<TD colspan=2 width=22%>&nbsp;</TD>");
				cols = cols + 2;
			}
			else if (cnt == 1)
			{
				sb.append("<TD colspan=2 width=22%>");
				sb.append("<SPAN class=black_ar_b>"
						+ ApplicationProperties.getValue(H2COL_LBLS[cnt]) + "</SPAN>");
				sb.append(TD_CLOSE);
				cols = cols + 2;
			}
			else
			{
				sb.append("<TD " + colspan + " width=" + (colsp * 15) + "% >");
				sb.append("<SPAN class=black_ar_b>"
						+ ApplicationProperties.getValue(H2COL_LBLS[cnt]) + "</SPAN>");
				sb.append(TD_CLOSE);
				cols = cols + colsp;
			}
		}
		sb.append("<TD colspan=" + (this.colNum - cols) + ">&nbsp;</TD>");
		sb.append(TR_CLOSE);
	}

	private String[] get1EleDetAt(int counter, GenericSpecimen specimen, String elementNamePrefix)
	{
		String str[] = new String[2];
		if (SpecimenDetailsNewFormat.HDR1_COLS[0].equalsIgnoreCase(this.columnList.get(counter)
				.toString()))
		{
			str[0] = elementNamePrefix + "parentName";
			str[1] = this.getFormattedValue(specimen.getParentName());
		}
		else if (SpecimenDetailsNewFormat.HDR1_COLS[1].equalsIgnoreCase(this.columnList
				.get(counter).toString()))
		{
			str[0] = elementNamePrefix + "displayName";
			str[1] = this.getFormattedValue(specimen.getDisplayName());
		}
		else if (SpecimenDetailsNewFormat.HDR1_COLS[2].equalsIgnoreCase(this.columnList
				.get(counter).toString()))
		{
			str[0] = elementNamePrefix + "barCode";
			str[1] = this.getFormattedValue(specimen.getBarCode());
		}
		else if (SpecimenDetailsNewFormat.HDR1_COLS[3].equalsIgnoreCase(this.columnList
				.get(counter).toString()))
		{
			str[0] = elementNamePrefix + "type";
			str[1] = this.getFormattedValue(specimen.getType());
		}
		else if (SpecimenDetailsNewFormat.HDR1_COLS[4].equalsIgnoreCase(this.columnList
				.get(counter).toString()))
		{
			str[0] = elementNamePrefix + "quantity";
			str[1] = this.getFormattedValue(specimen.getQuantity());
		}
		else if (SpecimenDetailsNewFormat.HDR1_COLS[5].equalsIgnoreCase(this.columnList
				.get(counter).toString()))
		{
			str[0] = elementNamePrefix + "concentration";
			str[1] = this.getFormattedValue(specimen.getConcentration());
		}
		else if (SpecimenDetailsNewFormat.HDR1_COLS[6].equalsIgnoreCase(this.columnList
				.get(counter).toString()))
		{
			str = new String[10];
			str[0] = elementNamePrefix + "selectedContainerName";
			str[1] = this.getFormattedValue(specimen.getSelectedContainerName());
			str[2] = elementNamePrefix + "positionDimensionOne";
			str[3] = this.getFormattedValue(specimen.getPositionDimensionOne());
			str[4] = elementNamePrefix + "positionDimensionTwo";
			str[5] = this.getFormattedValue(specimen.getPositionDimensionTwo());
			str[6] = elementNamePrefix + "containerId";
			str[7] = this.getFormattedValue(specimen.getContainerId());
			str[8] = elementNamePrefix + "storageContainerForSpecimen";
			str[9] = this.getFormattedValue(specimen.getStorageContainerForSpecimen());

			//if((specimen.getStorageContainerForSpecimen()!= null && specimen.getStorageContainerForSpecimen().equalsIgnoreCase("Virtual")) || (getFormattedValue(specimen.getSelectedContainerName()).trim().length() ==0))
			if ((specimen.getStorageContainerForSpecimen() != null && "Virtual"
					.equalsIgnoreCase(specimen.getStorageContainerForSpecimen())))
			{
				str[1] = this.getFormattedValue("");
				str[3] = this.getFormattedValue("");
				str[5] = this.getFormattedValue("");
				str[7] = this.getFormattedValue("");
			}
		}
		else if (SpecimenDetailsNewFormat.HDR1_COLS[7].equalsIgnoreCase(this.columnList
				.get(counter).toString()))
		{
			str[0] = elementNamePrefix + "checkedSpecimen";
			str[1] = this.getFormattedValue(specimen.getCheckedSpecimen());
		}
		//bug 11169 start
		else if (SpecimenDetailsNewFormat.HDR1_COLS[8].equalsIgnoreCase(this.columnList
				.get(counter).toString()))
		{
			str[0] = elementNamePrefix + "printSpecimen";
			str[1] = this.getFormattedValue(specimen.getPrintSpecimen());
		}
		//bug 11169 end
		return str;
	}

	private String[] get2EleDetAt(int counter, GenericSpecimen specimen, String elementNamePrefix)
	{
		final String str[] = new String[2];
		if (counter == 0)
		{
			str[0] = elementNamePrefix + "type";
			str[1] = this.getFormattedValue(specimen.getType());
		}
		else if (counter == 1)
		{
			str[0] = elementNamePrefix + "pathologicalStatus";
			str[1] = this.getFormattedValue(specimen.getPathologicalStatus());
		}
		else if (counter == 2)
		{
			str[0] = elementNamePrefix + "tissueSide";
			str[1] = this.getFormattedValue(specimen.getTissueSide());
		}
		else if (counter == 3)
		{
			str[0] = elementNamePrefix + "tissueSite";
			str[1] = this.getFormattedValue(specimen.getTissueSite());
		}

		return str;
	}

	private final int[] sizes = {8, 8, 8, 8, 8, 5, 8, 8, 8, 8};

	private void createFieldRow(StringBuffer sb, int counter, GenericSpecimen specimen,
			String elementNamePrefix, boolean isTextRow)
	{
		int tmpd = 0, tmpcwd = 0;

		if (this.dataListType.equalsIgnoreCase(this.dataListTypes[2]))
		{
			tmpd = 3;
		}
		tmpcwd = this.cWd;

		sb.append(TR_OPEN);
		this.createParentComponent(sb, specimen, elementNamePrefix, isTextRow);
		for (int columnCounter = 1; columnCounter < this.columnList.size(); columnCounter++)
		{
			if (columnCounter == 5)
			{
				this.cWd = this.cWd - tmpd;
			}
			else
			{
				this.cWd = tmpcwd;
			}

			final String[] nameValue = this
					.get1EleDetAt(columnCounter, specimen, elementNamePrefix);
			// to be displayed only in case of aliquots.	|| 		
			if ((columnCounter == 3 && !this.dataListType.equalsIgnoreCase(this.dataListTypes[1]))
					|| (columnCounter == 5 && !this.dataListType
							.equalsIgnoreCase(this.dataListTypes[2]))) // to be displayed only in case of derived.
			{
				continue; // should be passed to hidden elements
			}
			else if ((columnCounter == 1 && !(this.specimenSummaryForm.getShowLabel() && specimen
					.getShowLabel()))
					|| (columnCounter == 2 && !(this.specimenSummaryForm.getShowbarCode() && specimen
							.getShowBarcode())))
			{
				sb.append(TD_1HLF + "1" + TD_2HLF);
				sb.append(SPACE);
			}
			else if (columnCounter == 6)
			{

				if (isTextRow)
				{
					sb.append("<TD colspan=4 width=" + (60 - this.pWd + tmpd) + "% class='"
							+ STYLE_CLASS + "'>");//bug 11169
					if (nameValue[1].trim().length() > 0)
					{
						sb.append(nameValue[1]);
						sb.append(":");
						sb.append(nameValue[3]);
						sb.append(",");
						sb.append(nameValue[5]);
					}
					else
					{
						sb.append(this.getHTMLFormattedValue(specimen
								.getStorageContainerForSpecimen()));
					}
				}
				else
				{
					sb.append("<TD colspan=4 width=" + (50 - this.pWd + tmpd) + "% class='"
							+ STYLE_CLASS + "'>");//bug 11169
					if (this.dataListTypes[0].equalsIgnoreCase(this.dataListType)
							&& (this.specimenSummaryForm.isMultipleSpEditMode() && !this.specimenSummaryForm
									.getShowParentStorage()))
					{
						if (nameValue[1].trim().length() > 0)
						{
							sb.append(nameValue[1]);
							sb.append(":");
							sb.append(nameValue[3]);
							sb.append(",");
							sb.append(nameValue[5]);
						}
						else
						{
							sb.append(this.getHTMLFormattedValue(specimen
									.getStorageContainerForSpecimen()));
						}
						for (int ind1 = 0; ind1 < 9; ind1 += 2)
						{
							final String[] tmpAr = {nameValue[ind1], nameValue[ind1 + 1]};
							this.createHiddenElement(sb, tmpAr);
						}
					}
					else
					{
						this.createNewStorageComponent(sb, nameValue, specimen);
					}
				}
			}
			else if (columnCounter == 7)
			{
				sb.append(TD_1HLF + 3 + TD_2HLF);//bug 11169
				if (this.isParentList
						&& this.specimenSummaryForm.getSelectedSpecimenId().equals(
								specimen.getUniqueIdentifier()))
				{
					this.functionCall = "onclick=\"onClickCollected(this)\"";
				}
				else
				{
					this.functionCall = "";
				}
				this.createCollectedComponent(sb, nameValue, isTextRow);
				//addRemainingSpecimenElements(sb,elementNamePrefix,specimen, isTextRow);
			}
			//bug 11169 start
			else if (columnCounter == 8)
			{
				sb.append(TD_1HLF + 3 + TD_2HLF);
				this.functionCall = "";
				this.createPrintComponent(sb, nameValue);
				this.addRemainingSpecimenElements(sb, elementNamePrefix, specimen, isTextRow);
			}
			//bug 11169 end
			else
			{
				if (isTextRow && columnCounter == 1)
				{
					sb.append(TD_1HLF + 5 + TD_2HLF);
				}
				else
				{
					sb.append(TD_1HLF + this.cWd + TD_2HLF);
				}
				if (isTextRow || columnCounter == 3)
				{
					sb.append("<SPAN class=" + STYLE_CLASS + ">"
							+ this.getHTMLFormattedValue(nameValue[1]) + "</SPAN>");
				}
				else
				{
					this.createTextComponent(sb, nameValue, STYLE_CLASS, this.sizes[columnCounter]);
				}
			}
			sb.append(TD_CLOSE);
		}
		sb.append(TR_CLOSE);
	}

	private void createNewStorageComponent(StringBuffer sb, String[] nameValue,
			GenericSpecimen specimen)
	{

		//		 sb.append("<td class=\"black_ar_md\" >");

		final String specimenId = this.getFormattedValue(specimen.getUniqueIdentifier());
		final String specimenClass = this.getFormattedValue(specimen.getClassName());
		final Long collectionProtocolId = specimen.getCollectionProtocolId();

		final String containerId = "containerId_" + specimenId;
		final String selectedContainerName = "selectedContainerName_" + specimenId;
		final String positionDimensionOne = "positionDimensionOne_" + specimenId;
		final String positionDimensionTwo = "positionDimensionTwo_" + specimenId;
		final String specimenClassName = (String) specimenClass;
		final String cpId = this.getFormattedValue(collectionProtocolId);
		final String functionCall = "showMap('" + selectedContainerName + "','"
				+ positionDimensionOne + "','" + positionDimensionTwo + "','" + containerId + "','"
				+ specimenClassName + "','" + cpId + "')";
		final int scSize = 17 + this.xtra;
		final String sid = specimen.getUniqueIdentifier();
		String isDisabled = "";

		sb.append("<table style=\"font-size:1em\" size=\"100%\">");
		sb.append(TR_OPEN);
		sb.append(TD_OPEN);
		sb.append("");

		sb.append("<select name=\"" + nameValue[8]
				+ "\" size=\"1\" onchange=\"scForSpecimen(this,'" + sid + "','" + specimenClassName
				+ "')\" class=\"black_new_md\" id=\"" + nameValue[8] + "\">");

		if ("Virtual".equals(specimen.getStorageContainerForSpecimen()))
		{
			sb.append("<option value=\"Virtual\" selected=\"selected\">Virtual</option>");
			isDisabled = "disabled='disabled'";
		}
		else
		{
			sb.append("<option value=\"Virtual\">Virtual</option>");
		}
		if ("Auto".equals(specimen.getStorageContainerForSpecimen()))
		{
			sb.append("<option value=\"Auto\" selected=\"selected\">Auto</option>");
		}
		else
		{
			sb.append("<option value=\"Auto\">Auto</option>");
		}
		if ("Manual".equals(specimen.getStorageContainerForSpecimen()))
		{
			sb.append("<option value=\"Manual\" selected=\"selected\">Manual</option>");
		}
		else
		{
			sb.append("<option value=\"Manual\">Manual</option>");
		}
		sb.append("</select>");
		sb.append(TD_CLOSE);

		sb.append(TD_OPEN);
		sb.append("<input type=\"text\" name=\"" + nameValue[0] + "\" value=\"" + nameValue[1]
				+ "\" size=\"" + scSize
				+ "\" class=\"black_ar_md\" onmouseover=\" showTip(this.id)\"  id=\""
				+ selectedContainerName + "\" " + isDisabled + " >");
		sb.append(TD_CLOSE);
		sb.append(TD_OPEN);
		sb.append("<input type=\"text\" name=\"" + nameValue[2] + "\" value=\"" + nameValue[3]
				+ "\" size=\"2\" class=\"black_ar_md\" id=\"" + positionDimensionOne + "\" "
				+ isDisabled + " >");
		sb.append(TD_CLOSE);
		sb.append(TD_OPEN);
		sb.append("<input type=\"text\" name=\"" + nameValue[4] + "\" value=\"" + nameValue[5]
				+ "\" size=\"2\" class=\"black_ar_md\" id=\"" + positionDimensionTwo + "\" "
				+ isDisabled + " >");
		sb.append(TD_CLOSE);
		sb.append(TD_OPEN);
		sb.append("<a href=\"#\" onclick=\"" + functionCall + "\">");
		sb
				.append("<img src=\"images/Tree.gif\" border=\"0\" width=\"13\" height=\"15\" title=\'View storage locations\'>");
		sb.append("</a>");
		sb.append("<input type=\"hidden\" name=\"" + nameValue[6] + "\" value=\"" + nameValue[7]
				+ "\" id=\"" + containerId + "\">");
		sb.append(TD_CLOSE);
		sb.append(TR_CLOSE);
		sb.append("</table>");
	}

	private void createPrintComponent(StringBuffer sb, String[] nameValue)
	{
		if (this.specimenSummaryForm.getShowCheckBoxes())
		{
			if (Constants.TRUE.equalsIgnoreCase(nameValue[1]))
			{
				sb.append("<center><input type=\"checkbox\" name=\"" + nameValue[0]
						+ "\" value=\"true\" checked=\"checked\"></center>");
			}
			else
			{
				sb.append("<center><input type=\"checkbox\" name=\"" + nameValue[0]
						+ "\" value=\"on\"></center>");
			}
		}
		else
		{
			this.createHiddenElement(sb, nameValue);
		}
	}

	private void createCollectedComponent(StringBuffer sb, String[] nameValue, boolean isTextRow)
	{
		//		 sb.append("<td class=\"black_ar_md\" >");
		if (this.specimenSummaryForm.getShowCheckBoxes())
		{
			//			 if("getTextElement".equalsIgnoreCase(calledFrom))
			if (isTextRow)
			{
				sb.append("<center><input type=\"checkbox\" name=\"" + nameValue[0]
						+ "\" value=\"true\" disabled=\"true\" checked=\"checked\"></center>");
			}
			else
			{
				if (Constants.TRUE.equalsIgnoreCase(nameValue[1]))
				{
					sb.append("<center><input type=\"checkbox\" name=\"" + nameValue[0]
							+ "\" value=\"on\" checked=\"checked\" " + this.functionCall
							+ "></center>");
				}
				else
				{
					sb.append("<center><input type=\"checkbox\" name=\"" + nameValue[0]
							+ "\" value=\"on\" " + this.functionCall + "></center>");
				}
			}
		}
		else
		{
			//sb.append(getHTMLFormattedValue(""));
			this.createHiddenElement(sb, nameValue);
		}
		//			 sb.append(TD_CLOSE);
	}

	// Mandar : 4Dec08
	private void createTextFieldRow(StringBuffer sb, int counter, GenericSpecimen specimen,
			String elementNamePrefix)
	{

		String colspan = "";
		int cols = 0;
		int colsp = 1;
		if (this.dataListType.equalsIgnoreCase(this.dataListTypes[2]))
		{
			colspan = "colspan=2";
			colsp = 2;
		}
		sb.append(TR_OPEN);
		for (int cnt = 0; cnt < H2COL_LBLS.length; cnt++)
		{
			final String nameValue[] = this.get2EleDetAt(cnt, specimen, elementNamePrefix);

			if (this.dataListType.equalsIgnoreCase(this.dataListTypes[2]) && cnt > 1)
			{
				sb.append("<TD colspan=2 class='black_ar'>&nbsp;</TD>");
				cols = cols + 2;
			}
			else if (cnt == 1)
			{
				sb.append("<TD colspan=2 class='black_ar'>");
				sb.append(nameValue[1]);
				sb.append(TD_CLOSE);
				cols = cols + 2;
			}
			else
			{
				sb.append("<TD " + colspan + " class='black_ar'>");
				sb.append(nameValue[1]);
				sb.append(TD_CLOSE);
				cols = cols + colsp;
			}
		}
		sb.append("<TD colspan=" + (this.colNum - cols) + " class='black_ar'>&nbsp;");
		//			addRemainingSpecimenElements(sb,elementNamePrefix,specimen);
		sb.append(TD_CLOSE);
		sb.append(TR_CLOSE);

	}

	private void addRemainingSpecimenElements(StringBuffer sb, String elementNamePrefix,
			GenericSpecimen specimen, boolean isTextRow)
	{
		final String nameValue[][] = this.getRemainingSpecimenElementsData(specimen,
				elementNamePrefix);
		for (final String[] element : nameValue)
		{
			sb.append("<input type=\"hidden\" name=\"" + element[0] + "\" value=\"" + element[1]
					+ "\" id=\"" + element[0] + "\">");
		}

		if (isTextRow)
		{
			String nV[] = this.get1EleDetAt(1, specimen, elementNamePrefix);
			sb.append("<input type=\"hidden\" name=\"" + nV[0] + "\" value=\"" + nV[1] + "\" id=\""
					+ nV[0] + "\">");

			nV = this.get1EleDetAt(2, specimen, elementNamePrefix);
			sb.append("<input type=\"hidden\" name=\"" + nV[0] + "\" value=\"" + nV[1] + "\" id=\""
					+ nV[0] + "\">");

			nV = this.get1EleDetAt(4, specimen, elementNamePrefix);
			sb.append("<input type=\"hidden\" name=\"" + nV[0] + "\" value=\"" + nV[1] + "\" id=\""
					+ nV[0] + "\">");

			nV = this.get1EleDetAt(6, specimen, elementNamePrefix);
			sb.append("<input type=\"hidden\" name=\"" + nV[0] + "\" value=\"" + nV[1] + "\" id=\""
					+ nV[0] + "\">");
			sb.append("<input type=\"hidden\" name=\"" + nV[2] + "\" value=\"" + nV[3] + "\" id=\""
					+ nV[2] + "\">");
			sb.append("<input type=\"hidden\" name=\"" + nV[4] + "\" value=\"" + nV[5] + "\" id=\""
					+ nV[4] + "\">");
			sb.append("<input type=\"hidden\" name=\"" + nV[6] + "\" value=\"" + nV[7] + "\" id=\""
					+ nV[6] + "\">");
			sb.append("<input type=\"hidden\" name=\"" + nV[8] + "\" value=\"" + nV[9] + "\" id=\""
					+ nV[8] + "\">");

			nV = this.get1EleDetAt(7, specimen, elementNamePrefix);
			sb.append("<input type=\"hidden\" name=\"" + nV[0] + "\" value=\"" + nV[1] + "\" id=\""
					+ nV[0] + "\">");

			nV = this.get1EleDetAt(8, specimen, elementNamePrefix);//bug 11169
			sb.append("<input type=\"hidden\" name=\"" + nV[0] + "\" value=\"" + nV[1] + "\" id=\""
					+ nV[0] + "\">");
		}
	}

	private String[][] getRemainingSpecimenElementsData(GenericSpecimen specimen,
			String elementNamePrefix)
	{
		String str[][] = new String[9][2];
		if (!this.dataListTypes[2].equals(this.dataListType))
		{
			str = new String[10][2];
			str[9][0] = elementNamePrefix + "concentration";
			str[9][1] = this.getFormattedValue(specimen.getConcentration());
		}

		str[0][0] = elementNamePrefix + "collectionProtocolId";
		str[0][1] = this.getFormattedValue(specimen.getCollectionProtocolId());
		str[1][0] = elementNamePrefix + "readOnly";
		str[1][1] = this.getFormattedValue(specimen.getReadOnly());
		str[2][0] = elementNamePrefix + "uniqueIdentifier";
		str[2][1] = this.getFormattedValue(specimen.getUniqueIdentifier());
		str[3][0] = elementNamePrefix + "id";
		str[3][1] = this.getFormattedValue(specimen.getId());

		str[4][0] = elementNamePrefix + "type";
		str[4][1] = this.getFormattedValue(specimen.getType());
		str[5][0] = elementNamePrefix + "pathologicalStatus";
		str[5][1] = this.getFormattedValue(specimen.getPathologicalStatus());
		str[6][0] = elementNamePrefix + "tissueSide";
		str[6][1] = this.getFormattedValue(specimen.getTissueSide());
		str[7][0] = elementNamePrefix + "tissueSite";
		str[7][1] = this.getFormattedValue(specimen.getTissueSite());
		str[8][0] = elementNamePrefix + "className";
		str[8][1] = this.getFormattedValue(specimen.getClassName());

		if (!this.dataListTypes[2].equals(this.dataListType))
		{
			str[9][0] = elementNamePrefix + "concentration";
			str[9][1] = this.getFormattedValue(specimen.getConcentration());
		}

		return str;
	}

	private void createHiddenElement(StringBuffer sb, String[] nameValue)
	{
		//		 sb.append("<input type=\"hidden\" name=\""+nameValue[0]+"\" value=\""+nameValue[1]+"\">");
		sb.append("<input type=\"hidden\" name=\"" + nameValue[0] + "\" value=\"" + nameValue[1]
				+ "\" id=\"" + nameValue[0] + "\">");
	}

	// ------------ Mandar : 2Dec08 New Code for new formatUI end----------------

}
