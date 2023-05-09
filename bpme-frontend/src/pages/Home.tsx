import { Heading1, Heading3 } from "../components/Headings";

function Home() {
  return (
    <main className="flex-grow px-16">
      <div className="flex justify-center mt-8">
        <Heading1 lightVariant={false}>Welcome to BPME</Heading1>
      </div>
      <div className="w-3/5 mt-8 px-4">
        <Heading3 lightVariant={true}>
          Our goal is to automate quantitative methodologies to measure and
          assess business processes.
        </Heading3>
      </div>
      <div className="mt-24 flex">
        <img
          src="https://irp-cdn.multiscreensite.com/6208c3da/dms3rep/multi/desktop/evaluation+image.jpg"
          alt="Super dooper image"
          className="w-1/3 px-8"
        />
        <div className="ml-auto w-2/5 px-4">
          <Heading3 lightVariant={true}>
            Content related to the features this has, with the images cycling to
            show the functionality
          </Heading3>
        </div>
      </div>
    </main>
  );
}

export default Home;
