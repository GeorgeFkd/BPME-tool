import React from "react";
import Footer from "../components/Footer";
import { Box, Divider, Text, useBreakpointValue } from "@chakra-ui/react";

function Contact() {

  return <>
    <Box className="px-8 py-8">
      <div className="px-16 text-center py-8">
        <Header />
        <TeamMembers />
      </div>
    </Box>
    <Footer />
  </>;
}

function Header() {
  return <div className="flex flex-col h-full">
    <span className="text-h4 mb-8">This is a project that signals our goal to bring quantitative evaluation in Business Processes modeled with BPMN.</span>
    <span className="text-h4">Up until now we have analysed more than 2000 bpmn models and are planning to analyse 4000 more until the end of the year.</span>
    <span className="text-h6">(Links to papers:) </span>
  </div>
}

function Member({ name, title, img, LinkedIn }: { name: string, title: string, img: string, LinkedIn: string }) {
  return <div className="flex flex-col h-full items-center mt-8 mb-8">
    <img src={img} className="h-24 w-24 rounded-full" />
    <span className="text-h4">{name}</span>
    <span className="text-h5">{title}</span>
    <Text as={"a"} className="text-h6" textColor={"brand.mute"} textDecorationColor={"brand.mute"} textDecoration={"underline"} _hover={{ textColor: "brand.mute-accent" }} href={LinkedIn}>Βιογραφικό</Text>
  </div>
}

function TeamMembers() {
  const orientation = useBreakpointValue({ base: "horizontal", lg: "vertical" }) as "horizontal" | "vertical";

  return <div className="mt-24 flex flex-col mr-auto h-full w-full">
    <span className="text-h3">The BPME Team</span>
    <div className="flex flex-col md:flex-row justify-around w-full">
      <Member LinkedIn="https://www.linkedin.com/in/fakidis-georgios-4a344a224/" name="Fakidis Georgios" title="Software Engineer" img="https://media.licdn.com/dms/image/D4D03AQGJK96enfHsaw/profile-displayphoto-shrink_800_800/0/1683812362894?e=1690416000&v=beta&t=QJ7wth7tKUrR74vPt2Kc-CzTmP_ynr-vvLGUvwb856w" />
      <Divider height="inherit" orientation={orientation} colorScheme="teal" />

      <Member LinkedIn="https://www.uom.gr/assets/site/public/nodes/1546/15070-CV_KVergidis_11_2022.pdf" name="Vergidis Kostas" title="BPMN Expert" img="https://avatars.githubusercontent.com/u/22570441?v=4" />
      <Divider height="inherit" orientation={orientation} />

      <Member LinkedIn="https://www.linkedin.com/in/georgios-tsakalidis-80937a84/" name="Tsakalidis Giorgos" title="BP Postdoc" img="https://media.licdn.com/dms/image/D4D03AQFynC76rMusYQ/profile-displayphoto-shrink_200_200/0/1681759325167?e=1690416000&v=beta&t=MbjPyqUsHI9Go5aqCTIMgjmDVboMNjzYjRgJOxj585o" />
      <Divider height="inherit" orientation={orientation} />

    </div>
  </div>
}

export default Contact;
