
import "swiper/css";
import "swiper/css/navigation"
import "swiper/css/pagination"
import "swiper/css/scrollbar"
import "../App.css"
import { Swiper, SwiperSlide } from 'swiper/react';
import { Navigation, Pagination, Autoplay, Scrollbar, A11y, Thumbs } from 'swiper';
import { Button, useBreakpointValue } from "@chakra-ui/react";
import Footer from "../components/Footer";
import { ArrowForwardIcon } from "@chakra-ui/icons";
function Home() {
  const carouselStyle = useBreakpointValue({
    base: {
      width: "100%",
      height: "150px",
    },
    sm: {
      width: "100%",
      height: "200px",
    },
    md: {
      width: "70%",
      height: "400px",
    },
  })
  const imageStyles = useBreakpointValue({
    base: {
      width: "100%",
      height: "100px", aspectRation: "auto"
    },
    sm: {
      width: "100%",
      height: "150px", aspectRation: "auto"
    },
    md: {
      width: "70%",
      height: "300px", aspectRation: "auto"
    },
  })

  return (
    <>
      <main className="flex-grow px-16 ">
        <div className="flex justify-center mt-8">
          {/* <Heading1 lightVariant={false}>Welcome to BPME</Heading1> */}
          <span className="text-h3 md:text-h2 lg:text-h1">Welcome to BPME</span>
        </div>
        <div className="w-full md:w-3/5 mt-8 px-4">
          {/* <Heading3 lightVariant={true}>
          Our goal is to automate quantitative methodologies to measure and
          assess business processes.
        </Heading3> */}
          <span className="text-h5 md:text-h4 lg:text-h3 w-full md:w-auto">Our goal is to automate quantitative methodologies to measure and assess business processes.</span>
        </div>
        <div className="mt-12 lg:mt-24 flex flex-col md:flex-row items-center mx-auto">
          {/* { breakpoints={{ 320: { height: 150, }, 480: { height: 200, }, 768: { height: 300, }, 1024: { height: 600, } }}} */}
          <Swiper modules={[Navigation, Pagination, Autoplay, Scrollbar, A11y, Thumbs]} loop={true} autoplay={{ delay: 5000, stopOnLastSlide: true, disableOnInteraction: false }} slidesPerView={1} pagination={true} slidesPerGroup={1} style={carouselStyle} centeredSlides={true}  >
            <SwiperSlide className="swiper-slide">
              <img src="https://irp-cdn.multiscreensite.com/6208c3da/dms3rep/multi/desktop/evaluation+image.jpg" style={imageStyles} />
            </SwiperSlide>
            <SwiperSlide className="swiper-slide">            <img style={imageStyles} src="https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fd2myx53yhj7u4b.cloudfront.net%2Fsites%2Fdefault%2Ffiles%2FIC-BPM-Lifecycle-c.jpg&f=1&nofb=1&ipt=06326df5b884eec29ab72d268e3446c8db79ea2390136e986c749aa328bf2e68&ipo=images" className="w-1/3 px-8" />
            </SwiperSlide>
            <SwiperSlide className="swiper-slide">            <img style={imageStyles} src="https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.sweetprocess.com%2Fwp-content%2Fuploads%2F2017%2F12%2Fbusiness_process_management_3.jpg&f=1&nofb=1&ipt=deac177ff3f1e4e893cd9f24e7d9bef560f86d0145c88d82342e936cf36a5060&ipo=images" className="w-1/3 px-8" />
            </SwiperSlide>
            <SwiperSlide className="swiper-slide">            <img style={imageStyles} src="https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.notifyvisitors.com%2Fpb%2Fwp-content%2Fuploads%2F2020%2F12%2FBPM-Life-Cycle.jpg&f=1&nofb=1&ipt=41859726e31a913d4ad2428d9f0fbee3f42eab80d252f7ba80cac1b9d33378de&ipo=images" className="w-1/3 px-8" />
            </SwiperSlide>
            <SwiperSlide className="swiper-slide">            <img style={imageStyles} src="https://irp-cdn.multiscreensite.com/6208c3da/dms3rep/multi/desktop/evaluation+image.jpg" className="w-1/3 px-8" />
            </SwiperSlide>

          </Swiper>


          <div className="mt-12 md:mt-0 mx-auto md:ml-auto w-full md:w-2/5 px-4 mb-4">
            <span className="text-h5 md:text-h4 lg:text-h3 w-full md:w-auto">Content related to the features this has, with the images cycling to
              show the functionality</span>


          </div>
        </div>
        <div className="flex w-full ">

          <Button as="a" href="/tool" rightIcon={<ArrowForwardIcon w="8" h="8" />} mt="2" mb="2" width="200px" height="60px" justifySelf={"center"} mx="auto" bgColor={"brand.primary"} textColor={"white"} _hover={{ backgroundColor: "brand.primary-accent" }}>Use the Tool</Button>
        </div>

      </main >
      <Footer />
    </>
  );
}

export default Home;
