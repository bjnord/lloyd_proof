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
    let(:sync_id) { 555 }
    let(:correction) { mock(Hash) }
    let(:status) { Hash[:sync_id => sync_id] }
    before(:each) do
      Correction.should_receive(:create_and_return_status).twice.with(correction).and_return(status)
      post :upload, :corrections => [correction, correction], :format => :json
    end
    subject { controller }

    it { should respond_with(:success) }
    it { should assign_to(:statuses).with([status, status]) }
  end
end
