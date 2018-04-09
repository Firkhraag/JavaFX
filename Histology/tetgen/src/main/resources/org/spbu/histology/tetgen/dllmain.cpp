#define TETLIBRARY

#include "stdafx.h"
#include <jni.h>
#include "org_spbu_histology_tetgen_Tetgen.h"
#include "tetgen.h"
#include <iostream>

using namespace std;

BOOL APIENTRY DllMain(HMODULE hModule,
	DWORD  ul_reason_for_call,
	LPVOID lpReserved
)
{
	switch (ul_reason_for_call)
	{
	case DLL_PROCESS_ATTACH:
	case DLL_THREAD_ATTACH:
	case DLL_THREAD_DETACH:
	case DLL_PROCESS_DETACH:
		break;
	}
	return TRUE;
}

JNIEXPORT jobject JNICALL Java_org_spbu_histology_tetgen_Tetgen_tetrahedralization
(JNIEnv *env, jclass obj, jint numberOfNodes, jdoubleArray nodeList, jint numberOfFacets, jintArray numberOfPolygonsInFacet, jintArray numberOfHolesInFacet, jdoubleArray holeListInFacet, jintArray numberOfVerticesInPolygon, jintArray vertexList, jint numberOfHoles, jdoubleArray holeList, jint numberOfRegions, jdoubleArray regionList, jstring switches) {

	jdouble *c_nodeList = (*env).GetDoubleArrayElements(nodeList, 0);
	jint *c_numberOfPolygonsInFacet = (*env).GetIntArrayElements(numberOfPolygonsInFacet, 0);
	jint *c_numberOfHolesInFacet = (*env).GetIntArrayElements(numberOfHolesInFacet, 0);
	jdouble *c_holeListInFacet = (*env).GetDoubleArrayElements(holeListInFacet, 0);
	jint *c_numberOfVerticesInPolygon = (*env).GetIntArrayElements(numberOfVerticesInPolygon, 0);
	jint *c_vertexList = (*env).GetIntArrayElements(vertexList, 0);
	jdouble *c_holeList = (*env).GetDoubleArrayElements(holeList, 0);
	jdouble *c_regionList = (*env).GetDoubleArrayElements(regionList, 0);
	const char *c_switchesStr;
	c_switchesStr = (*env).GetStringUTFChars(switches, NULL);

	tetgenio in, out;
	tetgenio::facet *f;
	tetgenio::polygon *p;

	in.firstnumber = 1;

	in.numberofpoints = numberOfNodes;
	in.pointlist = new REAL[in.numberofpoints * 3];
	for (int i = 0; i < numberOfNodes * 3; i++) {
		in.pointlist[i] = c_nodeList[i];
	}

	in.numberoffacets = numberOfFacets;
	in.facetlist = new tetgenio::facet[in.numberoffacets];

	int numberOfVerticesCount = 0;
	int holeCount = 0;
	int vertexCount = 0;

	for (int i = 0; i < numberOfFacets; i++) {
		f = &in.facetlist[i];
		f->numberofpolygons = c_numberOfPolygonsInFacet[i];
		f->polygonlist = new tetgenio::polygon[f->numberofpolygons];
		f->numberofholes = c_numberOfHolesInFacet[i];
		if (c_numberOfHolesInFacet[i] == 0)
			f->holelist = NULL;
		else {
			f->holelist = new REAL[c_numberOfHolesInFacet[i] * 3];
			for (int j = 0; j < f->numberofholes * 3; j++) {
				f->holelist[j] = c_holeListInFacet[holeCount];
				holeCount++;
			}
		}
		for (int j = 0; j < f->numberofpolygons; j++) {
			p = &f->polygonlist[j];
			p->numberofvertices = c_numberOfVerticesInPolygon[numberOfVerticesCount];
			numberOfVerticesCount++;
			p->vertexlist = new int[p->numberofvertices];
			for (int k = 0; k < p->numberofvertices; k++) {
				p->vertexlist[k] = c_vertexList[vertexCount];
				vertexCount++;
			}
		}
	}

	in.numberofholes = numberOfHoles;
	if (numberOfHoles == 0)
		in.holelist = NULL;
	else {
		in.holelist = new REAL[numberOfHoles * 3];
		for (int i = 0; i < numberOfHoles * 3; i++)
			in.holelist[i] = c_holeList[i];
	}
	in.numberofregions = numberOfRegions;
	if (numberOfRegions == 0)
		in.regionlist = NULL;
	else {
		in.regionlist = new REAL[numberOfRegions * 5];
		for (int i = 0; i < numberOfHoles * 3; i++)
			in.holelist[i] = c_holeList[i];
	}

	jobject  tetgenResultObject;
	try {
		tetrahedralize((char *)c_switchesStr, &in, &out);
		jdoubleArray jPointList = (*env).NewDoubleArray(out.numberofpoints * 3);
		jintArray jTetrahedronList = (*env).NewIntArray(out.numberoftetrahedra * 4);
		jintArray jFaceList = (*env).NewIntArray(out.numberoftrifaces * 3);
		(*env).SetDoubleArrayRegion(jPointList, 0, out.numberofpoints * 3, out.pointlist);
		(*env).SetIntArrayRegion(jTetrahedronList, 0, out.numberoftetrahedra * 4, reinterpret_cast<jint*>(out.tetrahedronlist));
		(*env).SetIntArrayRegion(jFaceList, 0, out.numberoftrifaces * 3, reinterpret_cast<jint*>(out.trifacelist));
		jclass tetgenResultClass = (*env).FindClass("org/spbu/histology/tetgen/TetgenResult");
		jmethodID midConstructor = (*env).GetMethodID(tetgenResultClass, "<init>", "([D[I[I)V");
		tetgenResultObject = (*env).NewObject(tetgenResultClass, midConstructor, jPointList, jTetrahedronList, jFaceList);
	}
	catch (...) {
		jclass tetgenResultClass = (*env).FindClass("org/spbu/histology/tetgen/TetgenResult");
		jmethodID midConstructor = (*env).GetMethodID(tetgenResultClass, "<init>", "([D[I[I)V");
		tetgenResultObject = (*env).NewObject(tetgenResultClass, midConstructor, (*env).NewDoubleArray(0), (*env).NewIntArray(0), (*env).NewIntArray(0));
	}

	(*env).ReleaseDoubleArrayElements(nodeList, c_nodeList, 0);
	(*env).ReleaseDoubleArrayElements(holeList, c_holeList, 0);
	(*env).ReleaseDoubleArrayElements(holeListInFacet, c_holeListInFacet, 0);
	(*env).ReleaseIntArrayElements(numberOfHolesInFacet, c_numberOfHolesInFacet, 0);
	(*env).ReleaseIntArrayElements(numberOfPolygonsInFacet, c_numberOfPolygonsInFacet, 0);
	(*env).ReleaseIntArrayElements(numberOfVerticesInPolygon, c_numberOfVerticesInPolygon, 0);
	(*env).ReleaseIntArrayElements(vertexList, c_vertexList, 0);
	(*env).ReleaseDoubleArrayElements(regionList, c_regionList, 0);
	return  tetgenResultObject;
}
