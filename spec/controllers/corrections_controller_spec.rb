# Author::     Brent J. Nordquist <mailto:brent@nordist.net>
# Copyright::  Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
# License::    {CC Attribution-ShareAlike 3.0 Unported}[http://creativecommons.org/licenses/by-sa/3.0/]

require 'spec_helper'

describe CorrectionsController do

  describe '#index' do
    let(:corrections) { mock(Array) }
    before(:each) do
      Correction.should_receive(:all).and_return(corrections)
      get :index
    end
    subject { controller }

    it { should render_template(:index) }
    it { should assign_to(:corrections).with(corrections) }
  end

  describe '#upload' do

    shared_context "CorrectionsController#upload" do
      before(:each) do
        Correction.should_receive(:create_and_return_status).twice.with(attr).and_return(status)
        post :upload, :corrections => [attr, attr], :format => :json
      end
      subject { controller }
    end

    shared_examples_for "a successful upload" do
      it { should respond_with(:success) }
      it { should assign_to(:statuses).with([status, status]) }
    end

    context "with a sync_id" do
      let(:sync_id) { 555 }
      let(:attr) { Hash["sync_id" => sync_id] }
      let(:status) { Hash["sync_id" => sync_id] }
      include_context "CorrectionsController#upload"

      it_behaves_like "a successful upload"
    end

    context "without a sync_id" do
      let(:attr) { Hash[] }
      let(:status) { Hash[] }
      include_context "CorrectionsController#upload"

      it_behaves_like "a successful upload"
    end

  end
end
