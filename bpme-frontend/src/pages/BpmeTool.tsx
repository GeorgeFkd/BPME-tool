import { useDispatch, useSelector } from "react-redux";
import { addMetric, removeMetric, addAllMetrics, removeAllMetrics, toggleMetric } from "../store/metricsSlice";
import { addFiles, removeAllFiles, removeSpecificFile } from "../store/filesSlice";
import { RootState } from "../store/store";
import { Button, Menu, MenuButton, MenuItem, MenuList, Select, MenuItemOption, MenuOptionGroup, List, ListItem, ListIcon, Divider, FormControl, TableContainer, Table, Thead, Tr, Th, Tbody, Td } from "@chakra-ui/react";
import { ChevronDownIcon, MinusIcon, PlusSquareIcon } from "@chakra-ui/icons";
import { ALL_METRICS } from "../constants/metrics";
import { useState } from "react";
import { AnalysisResults, useAnalyzeBpmnFilesQuery } from "../api/analysis";
import { analyzeResultsAndOutputFileForUser } from "../utils/exportToExcel"

const supportedFileTypes = ["xml", "bpmn"]
function BpmeTool() {
  const [skip, setSkip] = useState(true);
  const dispatch = useDispatch();
  const metrics = useSelector((state: RootState) => state.metrics.metrics)
  const files = useSelector((state: RootState) => state.files.files)
  const { data, error, isLoading, isUninitialized, status } = useAnalyzeBpmnFilesQuery({ metrics, files }, { skip })

  console.log("The metrics", metrics)
  console.log("The files", files)
  console.log("FETCHING", data, error, isLoading, isUninitialized)


  const handleCalculateBtn = () => {
    setSkip(false)
    // dispatch(removeAllMetrics())
    // dispatch(removeAllFiles())
  }

  return <div className="h-screen px-16 w-100">
    <div className="px-4 py-4 flex flex-col">
      <Title />
      <SubmitFilesBtn />

      <div className="w-100 mt-8">
        <div className="flex flex-col lg:flex-row w-100 justify-between">
          <div className="flex flex-col lg:w-1/2">
            <span className="text-h5 block w-full lg:text-h4">Files Submitted: {files.length}</span>
            <SearchComponent />
            <FilesSubmittedPanel />
          </div>
          <div className="w-2 h-100 bg-slate-200"></div>
          <div className="lg:w-1/2">
            <div className="flex flex-col mt-6 lg:mt-0">


              <MetricsMenu />

              <DisplaySelectedMetrics />
              <div className="flex flex-col md:flex-row lg:flex-row gap-8 pt-4 my-4 justify-items-center">

                <button className="bg-secondary mx-auto px-6 w-2/3 md:w-1/2 lg:w-1/4  py-2 rounded-md hover:bg-green-600" onClick={() => dispatch(removeAllMetrics())}>Select None</button>

                <button className="bg-primary mx-auto w-2/3 md:w-1/2  lg:w-1/4  py-2 rounded-md text-white hover:bg-orange-500" onClick={() => dispatch(addAllMetrics())}>Select All</button>
                <button className="bg-purple-500 mx-auto py-3 px-16 rounded-md text-white hover:bg-purple-600" onClick={handleCalculateBtn}>Calculate</button>
              </div>

            </div>
          </div>
        </div>

      </div>
      {/* i have the problem where each metric ends up in a different place */}
      {data && <DisplayAnalysisResults data={data} />}
      {isLoading && <Spinner />}
      {isUninitialized && files.length > 0 && <div className="text-h3">{metrics.length === 0 && "Add metrics and "}Click Calculate to get results </div>}
    </div>
  </div>;
}

function Spinner() {
  return <div className="text-h3">Calculating...</div>
}


function DisplayAnalysisResults({ data }: { data: AnalysisResults }) {
  const metrics = useSelector((state: RootState) => state.metrics.metrics)
  return <>
    <div className="flex flex-row items-center md:gap-4">

      <span className="text-h5 md:text-h3">Analysis Results</span>
      <Button onClick={() => { analyzeResultsAndOutputFileForUser(data) }} className="bg-primary">Export to excel</Button>
    </div>
    <TableContainer>
      <Table variant={"striped"} maxWidth={"670px"}>
        <Thead>
          <Tr>
            <Th>Filename</Th>
            {metrics.slice().sort((a, b) => {

              if (a > b) return 1
              if (a < b) return -1
              return 0
            }

            ).map((metric, index) => <Th key={metric}>{metric}</Th>)}
          </Tr>
        </Thead>
        <Tbody>
          {data.metricResults.map((res, index) => (<Tr key={res.filename}>
            <Td>{res.filename}</Td>
            {res.results.slice().sort((a, b) => {
              if (a.metricName > b.metricName) return 1
              if (a.metricName < b.metricName) return -1
              return 0


            }).map((metricRes, index) => {
              return <Td key={metricRes.metricName}>{metricRes.result.toFixed(2)}</Td>
            })}

          </Tr>))
          }
          <Tr>
            <Td className="font-bold">MIN</Td>
            {data.statisticalResults.slice().sort((a, b) => {
              if (a.metricName > b.metricName) return 1
              if (a.metricName < b.metricName) return -1
              return 0
            }).map((statRes, index) => {
              return <Td key={statRes.metricName}>{statRes.statistics.min.toFixed(2)}</Td>
            })}
          </Tr>

          <Tr>
            <Td className="font-bold">MAX</Td>
            {data.statisticalResults.slice().sort((a, b) => {
              if (a.metricName > b.metricName) return 1
              if (a.metricName < b.metricName) return -1
              return 0
            }).map((statRes, index) => {
              return <Td key={statRes.metricName}>{statRes.statistics.max.toFixed(2)}</Td>
            })}
          </Tr>

          <Tr>
            <Td className="font-bold">MEAN</Td>
            {data.statisticalResults.slice().sort((a, b) => {
              if (a.metricName > b.metricName) return 1
              if (a.metricName < b.metricName) return -1
              return 0
            }).map((statRes, index) => {
              return <Td key={statRes.metricName}>{statRes.statistics.mean.toFixed(2)}</Td>
            })}
          </Tr>

          <Tr>
            <Td className="font-bold">SD</Td>
            {data.statisticalResults.slice().sort((a, b) => {
              if (a.metricName > b.metricName) return 1
              if (a.metricName < b.metricName) return -1
              return 0
            }).map((statRes, index) => {
              return <Td key={statRes.metricName}>{statRes.statistics.standardDeviation.toFixed(2)}</Td>
            })}
          </Tr>
        </Tbody>
      </Table>



    </TableContainer></>
}


export default BpmeTool;


function SearchComponent() {
  return <input type="text" placeholder="Search Files" className="border-2 px-4 py-3 rounded-xl w-56" />
}



function DisplaySelectedMetrics() {
  const metrics = useSelector((state: RootState) => state.metrics.metrics)

  return <div className="px-4">
    {metrics.map((metric, index) => <div key={metric} className="flex flex-col justify-between gap-2">
      <MetricLabel metric={metric} />
      <Divider />
    </div>)}
  </div>
}



function FilesSubmittedPanel() {
  const dispatch = useDispatch();
  const files = useSelector((state: RootState) => state.files.files)
  return <div className="flex w-full mt-6 lg:h-80 lg:w-96 flex-col bg-grey-25 scroll-auto rounded-md p-4 overflow-auto">
    <List spacing={4}>

      {files.map((file, index) => <>
        <ListItem key={file.name} className="flex justify-between items-center text-h5 hover:bg-grey-50 py-2 px-2" onClick={() => dispatch(removeSpecificFile(file.name))}>{file.name}
          <ListIcon as={MinusIcon} color="red.500" className="mr-8" />
        </ListItem>
        <Divider />
      </>)}
    </List>
  </div>
}



function SubmitFilesBtn() {
  const dispatch = useDispatch();
  const handleFilesSubmitClick = async (event: any) => {

    const target = event.target as HTMLInputElement;
    const files: FileList | null = target.files;
    if (files === null) {
      return;
    }
    Array.from(files).map((file) => console.log(file.name.split(".")[1]))
    const filteredFiles = Array.from(files).filter((file) => supportedFileTypes.includes(file.name.split(".")[1]))
    // const theobj = URL.createObjectURL(filteredFiles[0])
    dispatch(addFiles(filteredFiles))


  }
  return <label className="px-6 md:px-10 lg:px-16 mx-auto flex cursor-pointer hover:bg-slate-600 bg-primary invalid:bg-white valid:bg-black py-3 rounded-2xl">
    <input type="file" className="absolute bg-transparent" onChange={handleFilesSubmitClick} required style={{ top: "-10000px" }} multiple />
    <span className="mx-auto text-white text-h5">Submit Files</span>
  </label>
}



function MetricLabel({ metric }: { metric: string }) {

  return <span className="text-h5">{metric}</span>

}


function MetricsMenu() {
  const dispatch = useDispatch();
  const metrics = useSelector((state: RootState) => state.metrics.metrics)

  function isAlreadySelected(metric: string) {
    return metrics.includes(metric)
  }
  // TODO SYNC THIS WITH THE REDUX STORE
  // i can also group those metrics by category
  return <Menu closeOnSelect={false}>
    {({ forceUpdate }) =>
    (<>


      <MenuButton as={Button} rightIcon={<ChevronDownIcon />} className="text-h4 text-center mx-auto">
        Select Metrics
      </MenuButton>
      <MenuList>
        <MenuOptionGroup title="metrics" type="checkbox">

          {ALL_METRICS.map((metric) => <MenuItemOption key={metric} value={metric} onClick={() => dispatch(toggleMetric(metric))}>{metric}</MenuItemOption>)}
        </MenuOptionGroup>
      </MenuList>

    </>)
    }

  </Menu>
}


function Title() {
  return <div className="lg:px-16 mx-auto text-h5 md:text-h4 lg:text-h3 w-2/3 text-center my-6">Calculate Metrics in BPMN diagrams</div>;
}

