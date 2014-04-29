package com.webpagebytes.cms;

import static org.junit.Assert.*;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.webpagebytes.cms.ModelBuilder;
import com.webpagebytes.cms.PageContentBuilder;
import com.webpagebytes.cms.appinterfaces.WBPageModelProvider;
import com.webpagebytes.cms.appinterfaces.WBModel;
import com.webpagebytes.cms.cache.WBCacheInstances;
import com.webpagebytes.cms.cache.WBWebPagesCache;
import com.webpagebytes.cms.cmsdata.WBProject;
import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.exception.WBException;
import com.webpagebytes.cms.exception.WBTemplateException;
import com.webpagebytes.cms.template.WBTemplateEngine;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PageContentBuilder.class})
public class TestPageContentBuilder {

WBCacheInstances cacheInstancesMock;
WBWebPagesCache pagesCacheMock;
PageContentBuilder pageContentBuilder;
ModelBuilder modelBuilderMock;
WBTemplateEngine templateEngineMock;
HttpServletRequest requestMock;
WBWebPage pageMock;
WBProject projectMock;

@Before
public void setUp()
{
	cacheInstancesMock = EasyMock.createMock(WBCacheInstances.class);
	pagesCacheMock = EasyMock.createMock(WBWebPagesCache.class);
	modelBuilderMock = EasyMock.createMock(ModelBuilder.class);
	pageContentBuilder = new PageContentBuilder(cacheInstancesMock, modelBuilderMock);
	templateEngineMock = EasyMock.createMock(WBTemplateEngine.class);
	requestMock = EasyMock.createMock(HttpServletRequest.class);
	pageMock = EasyMock.createMock(WBWebPage.class);
	projectMock = EasyMock.createMock(WBProject.class);
	Whitebox.setInternalState(pageContentBuilder, "templateEngine", templateEngineMock);
}

@Test
public void test_initialize()
{
		
	try
	{
		templateEngineMock.initialize();
		EasyMock.replay(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock);	
		pageContentBuilder.initialize();
	}catch (Exception e)
	{
		assertTrue (false);
	}
	
	EasyMock.verify(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock);
}

@Test
public void test_find_web_page()
{
		
	try
	{
		String pageExternalKey = "123";
		EasyMock.expect(cacheInstancesMock.getWBWebPageCache()).andReturn(pagesCacheMock);
		EasyMock.expect(pagesCacheMock.getByExternalKey(pageExternalKey)).andReturn(pageMock);		
		EasyMock.replay(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock);	
		WBWebPage result = pageContentBuilder.findWebPage(pageExternalKey);
		assertTrue (result == pageMock);
		
	}catch (Exception e)
	{
		assertTrue (false);
	}
	
	EasyMock.verify(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock);
}


@Test
public void test_buildPageContent_null_isTemplateSource()
{
	
	try
	{
		WBModel model = new WBModel();
		String htmlSource = "<html>text</html>";
		EasyMock.expect(pageMock.getIsTemplateSource()).andReturn(null);
		EasyMock.expect(pageMock.getHtmlSource()).andReturn(htmlSource);
		EasyMock.replay(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);	
		String result = pageContentBuilder.buildPageContent(requestMock, pageMock, model);
		EasyMock.verify(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);		
		assertTrue (result.equals(htmlSource));

	}catch (Exception e)
	{
		assertTrue (false);
	}	
}

@Test
public void test_buildPageContent_zero_isTemplateSource()
{
	
	try
	{
		WBModel model = new WBModel();
		String htmlSource = "<html>text</html>";
		EasyMock.expect(pageMock.getIsTemplateSource()).andReturn(0);
		EasyMock.expect(pageMock.getHtmlSource()).andReturn(htmlSource);
		EasyMock.replay(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);	
		String result = pageContentBuilder.buildPageContent(requestMock, pageMock, model);
		EasyMock.verify(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);		
		assertTrue (result.equals(htmlSource));

	}catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_buildPageContent_zero_ok_nullController()
{
	
	try
	{
		WBModel model = new WBModel();
		String htmlSource = "<html>text</html>";
		String pageName = "index";
		
		EasyMock.expect(pageMock.getIsTemplateSource()).andReturn(1);

		modelBuilderMock.populateModelForWebPage(pageMock, model);
		
		EasyMock.expect(pageMock.getPageModelProvider()).andReturn(null);
		EasyMock.expect(pageMock.getName()).andReturn(pageName);
		
		templateEngineMock.process(EasyMock.anyObject(String.class), EasyMock.anyObject(Map.class), EasyMock.anyObject(Writer.class));
		
		EasyMock.replay(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);	
		String result = pageContentBuilder.buildPageContent(requestMock, pageMock, model);
		EasyMock.verify(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);		
		

	}catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_buildPageContent_validController()
{
	
	try
	{
		WBModel model = new WBModel();
		Map<String, String> locale = new HashMap<String, String>();
		locale.put(ModelBuilder.LOCALE_COUNTRY_KEY, "");
		locale.put(ModelBuilder.LOCALE_LANGUAGE_KEY, "en");
		model.getCmsModel().put(ModelBuilder.LOCALE_KEY, locale);
		
		String pageName = "index";
		String controllerClass = "com.webpagebytes.cms.DummyPageModelProvider";
		
		EasyMock.expect(pageMock.getIsTemplateSource()).andReturn(1);

		modelBuilderMock.populateModelForWebPage(pageMock, model);
		
		EasyMock.expect(pageMock.getPageModelProvider()).andReturn(controllerClass);
		
		EasyMock.expect(pageMock.getName()).andReturn(pageName);
		
		templateEngineMock.process(EasyMock.anyObject(String.class), EasyMock.anyObject(Map.class), EasyMock.anyObject(Writer.class));
		
		EasyMock.replay(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);	
		String result = pageContentBuilder.buildPageContent(requestMock, pageMock, model);
		EasyMock.verify(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);		
		

	}catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_buildPageContent_zero_ok_emptyController()
{
	
	try
	{
		WBModel model = new WBModel();
		String htmlSource = "<html>text</html>";
		String pageName = "index";
		
		EasyMock.expect(pageMock.getIsTemplateSource()).andReturn(1);

		modelBuilderMock.populateModelForWebPage(pageMock, model);
		
		EasyMock.expect(pageMock.getPageModelProvider()).andReturn("");
		EasyMock.expect(pageMock.getName()).andReturn(pageName);
		
		templateEngineMock.process(EasyMock.anyObject(String.class), EasyMock.anyObject(Map.class), EasyMock.anyObject(Writer.class));
		
		EasyMock.replay(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);	
		String result = pageContentBuilder.buildPageContent(requestMock, pageMock, model);
		EasyMock.verify(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);		
		

	}catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_buildPageContent_templateException()
{
	
	try
	{
		WBModel model = new WBModel();
		String htmlSource = "<html>text</html>";
		String pageName = "index";
		
		EasyMock.expect(pageMock.getIsTemplateSource()).andReturn(1);

		modelBuilderMock.populateModelForWebPage(pageMock, model);
		
		EasyMock.expect(pageMock.getPageModelProvider()).andReturn(null);
		EasyMock.expect(pageMock.getName()).andReturn(pageName);
		
		templateEngineMock.process(EasyMock.anyObject(String.class), EasyMock.anyObject(Map.class), EasyMock.anyObject(Writer.class));
		EasyMock.expectLastCall().andThrow(new WBTemplateException(""));
		EasyMock.replay(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);	
		String result = pageContentBuilder.buildPageContent(requestMock, pageMock, model);
				
	}
	catch (WBTemplateException e)
	{
		// OK
	}
	catch (Exception e)
	{
		assertTrue (false);
	}
	EasyMock.verify(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);
}

@Test
public void test_getPageModelProvider_exists_in_map()
{
	String controllerClass = "com.webpagebytes.cms.DefaultPageModelProvider";
	Map<String, WBPageModelProvider> customControllers = new HashMap<String, WBPageModelProvider>();
	WBPageModelProvider instController = new DummyPageModelProvider();
	customControllers.put(controllerClass, instController);	
	Whitebox.setInternalState(pageContentBuilder, "customControllers", customControllers);
	
	try
	{
		WBPageModelProvider result = Whitebox.invokeMethod(pageContentBuilder, "getPageModelProvider", controllerClass);
		
		assertTrue (result == instController);
	} catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_getPageModelProvider_not_exists_in_map()
{
	String controllerClass = "com.webpagebytes.cms.DummyPageModelProvider";
	try
	{
		WBPageModelProvider result = Whitebox.invokeMethod(pageContentBuilder, "getPageModelProvider", controllerClass);		
		assertTrue (result != null);
		
		Map<String, WBPageModelProvider> controllers = Whitebox.getInternalState(pageContentBuilder, "customControllers");
		assertTrue (controllers.get(controllerClass) != null);
	} catch (Exception e)
	{
		assertTrue (false);
	}	
}


@Test
public void test_getPageModelProvider_exception()
{
	String controllerClass = "com.webpagebytes.cms.DoesNotExistsModelProvider";	
	try
	{
		Whitebox.invokeMethod(pageContentBuilder, "getPageModelProvider", controllerClass);		
	} 
	catch (WBException e)
	{
		// OK
	}
	catch (Exception e)
	{
		assertTrue (false);
	}
	
}

}
