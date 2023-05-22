import { Box, Button, Divider, Input, Text, Textarea, useBreakpointValue } from "@chakra-ui/react";
import React from "react";

function Footer() {
  const orientation = useBreakpointValue({ base: "horizontal", lg: "vertical" }) as "horizontal" | "vertical";
  return <div className="transparent pt-4 px-4 fixed left-0 bottom-0 w-full h-42 flex flex-col lg:flex-row gap-4 border-t-2 border-black justify-between" >
    <NewsLetter />
    <Divider height="inherit" orientation={orientation} />
    <FindUsOnSocials />
    <Divider height="inherit" orientation={orientation} />
    <ExtraFooterInfo />
  </div>;
}

export default Footer;

function NewsLetter() {
  return <div className="flex flex-col lg:flex-row gap-4 items-center lg:items-start " >
    <div className="flex flex-col">
      <span className="text-h5">Sign up to our newsletter</span>
      <span className="text-black-50">info related to unsubscribing</span>
    </div>
    <div className="flex flex-col">
      <Text>Name</Text>
      <Input type="text" />
      <Text>Email</Text>
      <Input type="text" />

    </div>
    <div className="flex flex-col py-2 items-center">
      <Textarea placeholder="Tell us what you are looking for" w={["250px", "350px"]} py={"2"} my={"2"} h={["50%", "75%"]} />
      <Button className="mt-2 mx-auto px-6 py-2 w-2/3" bgColor={"brand.primary"} borderRadius={"2xl"} textColor={"white"} _hover={{ backgroundColor: "brand.primary-accent" }}>Subscribe</Button>
    </div>
  </ div>;
}

function FindUsOnSocials() {
  return <div className="flex flex-col gap-2 items-center lg:items-start" style={{ flexGrow: "2.5" }}>
    <span className="text-h5">Find us on Social Media</span>
    <Text as={"a"} href="https://www.linkedin.com/in/fakidis-georgios-4a344a224/" textColor={"brand.mute"} textDecorationColor={"brand.mute"} textDecoration={"underline"} className="text-h6" _hover={{ textColor: "brand.mute-accent" }}>LinkedIn</Text>
    <Text as={"a"} href="https://twitter.com/" textColor={"brand.mute"} textDecorationColor={"brand.mute"} textDecoration={"underline"} className="text-h6" _hover={{ textColor: "brand.mute-accent" }}>Twitter</Text>
    <Text as={"a"} href="https://www.researchgate.net/" textColor={"brand.mute"} textDecorationColor={"brand.mute"} textDecoration={"underline"} className="text-h6" _hover={{ textColor: "brand.mute-accent" }}>ResearchGate</Text>

  </div>;
}

function ExtraFooterInfo() {
  return <div className="" style={{ flexGrow: "2.5" }}>This site was designed by George Fakidis</div>;
}
